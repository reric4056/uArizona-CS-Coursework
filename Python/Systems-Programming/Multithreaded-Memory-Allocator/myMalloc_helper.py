"""
CSc 422
Program 2: myMalloc-helper

# Author: Eric Romero
# Note: This is the primary version that was provided within the zip files. I will be working on this as the days progress.

The main file caches list references like:
    _free_small_list = _mem_manager.free_small
    _free_large_list = _mem_manager.free_large
Then, it passes them to these functions as parameters.
"""

import threading
from typing import Optional

DEBUG = False

class Chunk:
    """
    A chunk represents a memory block with metadata.
    """
    def __init__(self, alloc_size: int = -1):
        self.alloc_size = alloc_size
        self.data: Optional[bytearray] = None
        self.is_overflow: bool = False
        self.thread_id = -1

def setup_chunks(free_list: list, num: int, block_size: int) -> None:
    """Create chunks and add them to a free list."""
    for _ in range(num):
        chunk = Chunk(alloc_size=block_size)
        chunk.data = bytearray(block_size)
        free_list.append(chunk)

def get_chunk(free_list: list) -> Chunk:
    """
    Get a chunk from the appropriate free list.
    
    free_list is the cached reference passed from myMalloc.py
    """
    if not free_list:
        return None
    chunk = free_list.pop()
    
    return chunk


def return_chunk(free_list: list, chunk: Chunk) -> None:
    """
    Return a chunk to the appropriate free list.
    
    free_list is the cached reference passed from myMalloc.py
    """
    # Return to thread's free list 
    free_list.append(chunk)

def assign_chunk(free_list: list, thread_id: int, is_overflow: bool = False) -> None:
    """
    Assigns respective attributes to a chunk depending on the program variation performed
    """
    for chunk in free_list:
        chunk.thread_id = thread_id
        chunk.is_overflow = is_overflow