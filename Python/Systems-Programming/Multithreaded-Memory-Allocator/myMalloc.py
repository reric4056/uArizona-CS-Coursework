"""
CSc 422
Program 2: Multithreaded Memory Allocator

# Author: Eric Romero
# Note: This is version is only adapted to complete the following tasks:
# 1. Single-Threaded Memory Allocator             -->  YES
# 2. Coarse-Grain, Concurrent Memory Allocator    -->  YES
# 3. Fine-Grain, Concurrent Memory Allocator      -->  NO

API:
    myInit(num_cores, flag) -> int
    myMalloc(size) -> bytearray
    myFree(ptr) -> None
"""

import threading
from typing import Optional

from myMalloc_helper import (
    Chunk, setup_chunks, get_chunk, return_chunk, assign_chunk
)

# Constants
SIZE_TOTAL = 276672
SIZE_SMALL = 64
SIZE_LARGE = 1024
SIZE_HALF = SIZE_TOTAL // 2

class MemoryManager:
    """
    Memory manager using Python lists.
    """
    def __init__(self):
        # Number of threads 
        self.num_threads = -1

        # Flag regarding what version we are dealing with
        self.flag = -1

        # Small Free list
        self.free_small = []
        
        # Large Free List
        self.free_large = []
        
        # Per-thread dictionaries for O(1) lookup: id(bytearray) -> chunk
        self.data_to_chunk = {}

        # Locks for overflow and ids
        self.overflow_lock = threading.Lock()
        self.id_lock = threading.Lock()

        # Thread local
        self.thread_local = threading.local()

        # ID assignment
        self.next_thread = 1

        # For file creation
        self.files = set()

# Global state
_mem_manager: Optional[MemoryManager] = None

# Cached references - set once in myInit to avoid repeated access overhead
_free_small_list = None
_free_large_list = None
_data_to_chunk_dict = None
_id_lock = None
_overflow_lock = None
_thread_local = None
_next_thread = None
_num_threads = 0
_flag = 0

def myInit(numThreads: int, flag: int) -> int:
    """
    Initialize the memory allocator.
 
    Args:
        numThreads: Number of cores/threads that will be used
        flag: 1 for coarse-grain (single locked list)
              2 for fine-grain (per-core with overflow)

    Note that flag is not used here because this is only the sequential version
    
    Returns:
        0 on success, -1 on failure (failure will never happen)
    """
    global _mem_manager
    global _free_small_list, _free_large_list, _data_to_chunk_dict
    global _id_lock, _overflow_lock, _thread_local
    global _num_threads, _flag
    
    # Create memory manager (elegant class-based design)
    _mem_manager = MemoryManager()

    # Initialize parameters
    _mem_manager.num_threads = numThreads
    _mem_manager.flag = flag

    _num_threads = numThreads
    _flag = flag

    # Creating additional lists for fine grain
    for _ in range(_num_threads + 1):
        _mem_manager.free_small.append([])
        _mem_manager.free_large.append([])
    
    # Instead of repeatedly accessing _mem_manager.{attribute} (which has overhead
    # in multi-threaded code), we cache the reference once and use it directly.
    _free_small_list = _mem_manager.free_small
    _free_large_list = _mem_manager.free_large
    _data_to_chunk_dict = _mem_manager.data_to_chunk
    _overflow_lock = _mem_manager.overflow_lock
    _id_lock = _mem_manager.id_lock
    _thread_local = _mem_manager.thread_local

    # Calculate number of chunks -
    CHUNK_OVERHEAD = 24 
    num_small = (SIZE_HALF) // (SIZE_SMALL + CHUNK_OVERHEAD)    
    num_large = (SIZE_HALF) // (SIZE_LARGE + CHUNK_OVERHEAD)

    # Case 1: Single Thread
    if (numThreads == 1):
        setup_chunks(_free_small_list[1], num_small, SIZE_SMALL)
        setup_chunks(_free_large_list[1], num_large, SIZE_LARGE)

        assign_chunk(_free_small_list[1], thread_id=1, is_overflow=False)
        assign_chunk(_free_large_list[1], thread_id=1, is_overflow=False)

        return 0

    # Case 2: Course Grain 
    if (flag == 1):
        setup_chunks(_free_small_list[0], num_small, SIZE_SMALL)
        setup_chunks(_free_large_list[0], num_large, SIZE_LARGE)

        assign_chunk(_free_small_list[0], thread_id=0, is_overflow=True)
        assign_chunk(_free_large_list[0], thread_id=0, is_overflow=True)

        return 0
    
    # Case 3: Fine grain
    if (flag == 2):
        local_lists_small = num_small // numThreads
        local_lists_large = num_large // numThreads

        active_lists_small = local_lists_small * numThreads
        active_lists_large = local_lists_large * numThreads

        overflow_list_small = num_small - active_lists_small
        overflow_list_large = num_large - active_lists_large

        # Initialize overflow lists
        setup_chunks(_free_small_list[0], overflow_list_small, SIZE_SMALL)
        setup_chunks(_free_large_list[0], overflow_list_large, SIZE_LARGE)

        assign_chunk(_free_small_list[0], thread_id=0, is_overflow=True)
        assign_chunk(_free_large_list[0], thread_id=0, is_overflow=True) 

        for i in range(1, numThreads + 1):
            setup_chunks(_free_small_list[i], local_lists_small, SIZE_SMALL)
            setup_chunks(_free_large_list[i], local_lists_large, SIZE_LARGE)

            assign_chunk(_free_small_list[i], thread_id=i, is_overflow=False)
            assign_chunk(_free_large_list[i], thread_id=i, is_overflow=False)

        return 0
    
    return -1

def myMalloc(size: int) -> Optional[bytearray]:
    """
    Allocate memory of the requested size.
    
    Args:
        size: Number of bytes to allocate
    
    Returns:
        bytearray of appropriate size
    """
    # Empty list for either small or large list
    requested_list = None
    # Just a base case, but unlikely this would happen
    if (size <= 0):
       return None
    
    # Variable 'size' is between 0 and 64, making it a small list
    if size <= SIZE_SMALL:
        designated_list = _free_small_list
    
    # Variable 'size' is between 65 and 276672, making it a large list
    elif size <= SIZE_LARGE:
        designated_list = _free_large_list

    # Another case, variable 'size' is larger than 276672, which is unlikely to happen as well.
    else:
        return None
        
    # Case 1: Single Thread
    if _num_threads == 1:
        with _overflow_lock:
            # Use cached reference
            chunk = get_chunk(designated_list[1])
            if chunk is None:
                return None
            
            # Use cached reference - no _mem_manager.attribute access
            _data_to_chunk_dict[id(chunk.data)] = chunk
            return chunk.data
        
    # Case 2: Coarse grain
    if _flag == 1:
        with _overflow_lock:
            # Use cached reference
            chunk = get_chunk(designated_list[0])
            if chunk is None:
                return None
            
            # Use cached reference - no _mem_manager.attribute access
            _data_to_chunk_dict[id(chunk.data)] = chunk
            return chunk.data        

    if _flag == 2:
        thread_id = get_thread_id()

        chunk = get_chunk(designated_list[thread_id])
        if chunk is not None:
            # Use cached reference - no _mem_manager.attribute access
            _data_to_chunk_dict[id(chunk.data)] = chunk
            return chunk.data
        
        with _overflow_lock:
            chunk = get_chunk(designated_list[0])
            if chunk is None:
                return None
            # Use cached reference - no _mem_manager.attribute access
            _data_to_chunk_dict[id(chunk.data)] = chunk
            return chunk.data
    
    return None

def myFree(ptr: bytearray) -> None:
    """
    Free previously allocated memory.
    
    Args:
        ptr: bytearray previously returned by myMalloc
    """
    
    # Use cached reference - no _mem_manager.attribute access
    chunk = _data_to_chunk_dict.pop(id(ptr))

    # Determines whether to free a small or large list chunk
    if chunk.alloc_size == SIZE_SMALL:
        designated_list = _free_small_list
    else:
        designated_list = _free_large_list

    if chunk.thread_id == 0:
        with _overflow_lock:
            return_chunk(designated_list[0], chunk)
    else:
        return_chunk(designated_list[chunk.thread_id], chunk)

def get_thread_id() -> int:
    thread_id = getattr(_thread_local, 'thread_id', None)
    if thread_id is not None:
        return thread_id
    
    with _id_lock:
        thread_id = getattr(_thread_local, 'thread_id', None)
        if thread_id is None:
            _thread_local.thread_id = _mem_manager.next_thread
            _mem_manager.next_thread += 1
    
    return _thread_local.thread_id

# Export API functions
__all__ = ['myInit', 'myMalloc', 'myFree']
