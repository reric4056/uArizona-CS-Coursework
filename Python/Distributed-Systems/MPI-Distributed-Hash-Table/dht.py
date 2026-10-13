# author: Eric Romero
# Implemented GET, PUT, ADD and REMOVE(without redistribution)
# Trying to follow suggested design approach

import sys
from mpi4py import MPI #mpi4py library
from dht_globals import * #global variables
from command import commandNode #command node code

# on an END message, the head node is to contact all storage nodes and tell them
def headEnd():

    # tell all the storage nodes to END
    # the data sent is unimportant here, so just send a dummy value
    dummy = MPI.COMM_WORLD.recv(source=numProcesses-1, tag=END)
    for i in range(1,numProcesses-1):
        MPI.COMM_WORLD.send(dummy, dest=i, tag=END)
    MPI.Finalize()
    sys.exit(0)
    
# on an END message, a storage node just calls MPI_Finalize and exits
def storageEnd(): 

    # the data is unimportant for an END
    dummy = MPI.COMM_WORLD.recv(source=0, tag=END)
    MPI.Finalize()
    sys.exit(0)

def getKeyVal(source):
    global table

    #receive the GET message
    #note that at this point, we've only called MPI_Probe, which only peeks at the message
    #we are receiving the key from whoever sent us the message 
    key = MPI.COMM_WORLD.recv(source=source, tag=GET)
    
    if key <= myStorageId:

        # find the associated value (called "value") using whatever data structure you use
        # you must add this code to find it (omitted here)
        value = table[key]

        # allocate a tuple with two integers: the first will be the value, the second will be this storage id
        argsAdd = (value, myStorageId)
        MPI.COMM_WORLD.send(argsAdd, dest=childRank, tag=RETVAL)
    else:
        MPI.COMM_WORLD.send(key, dest=childRank,tag=GET)

def putKeyVal(source):
    # This method follows a similar structure to getKeyVal()
    global table

    # Recieve the PUT message: sending a [key, val] pair
    pair = MPI.COMM_WORLD.recv(source=source, tag=PUT)

    key = pair[0]
    val = pair[1]

    if key <= myStorageId:
        table[key] = val

        # Should send an ACK around the chain
        dummy = 0

        MPI.COMM_WORLD.send(dummy, dest=childRank, tag=ACK)
    else:
        MPI.COMM_WORLD.send(pair, dest=childRank, tag=PUT)

def addNode(source):
    global childRank
    global childStorageId

    argsAdd = MPI.COMM_WORLD.recv(source=source, tag=ADD)

    newRank = argsAdd[0]
    newId = argsAdd[1]

    if newId <= childStorageId:
        oldChildRank = childRank
        oldChildStorageId = childStorageId

        childRank = newRank
        childStorageId = newId

        argsTrigger = [newId, oldChildRank, oldChildStorageId]
        MPI.COMM_WORLD.send(argsTrigger, dest=newRank, tag=ADD_NODE)
    
    else:
        MPI.COMM_WORLD.send(argsAdd, dest=childRank, tag=ADD)

def triggerNode(source):
    global table
    global myStorageId
    global childRank
    global childStorageId

    argsTrigger = MPI.COMM_WORLD.recv(source=source, tag=ADD_NODE)

    myStorageId = argsTrigger[0]
    childRank = argsTrigger[1]
    childStorageId = argsTrigger[2]

    table = {}

    dummy = 0

    MPI.COMM_WORLD.send(dummy, dest=childRank, tag=ACK)

def removeNode(source):
    global childRank
    global childStorageId

    currId = MPI.COMM_WORLD.recv(source=source, tag=REMOVE)

    if currId == childStorageId:
        oldChildRank = childRank

        dummy = 0

        MPI.COMM_WORLD.send(dummy, dest=oldChildRank, tag=REMOVE_NODE)

        response = MPI.COMM_WORLD.recv(source=oldChildRank, tag=ACK)

        childRank = response[0]
        childStorageId = response[1]

        MPI.COMM_WORLD.send(dummy, dest=childRank, tag=ACK)

    else:
        MPI.COMM_WORLD.send(currId, dest=childRank, tag=REMOVE)

def removeCurrNode(source):
    global table
    global myStorageId
    global childRank
    global childStorageId

    dummy = MPI.COMM_WORLD.recv(source=source, tag=REMOVE_NODE)

    oldChildRank = childRank
    oldChildStorageId = childStorageId

    table = {}
    myStorageId = None
    childRank = None
    childStorageId = None

    response = [oldChildRank, oldChildStorageId]
    MPI.COMM_WORLD.send(response, dest=source, tag=ACK)

def handleMessages():

    status = MPI.Status() # get a status object
    
    while True:
        # Peek at the message
        MPI.COMM_WORLD.probe( source=MPI.ANY_SOURCE, tag=MPI.ANY_TAG, status=status)
              
        # get the source and the tag---which MPI rank sent the message, and
        # what the tag of that message was (the tag is the command)
        source = status.Get_source()
        tag = status.Get_tag()

        # now take the appropriate action
        # code for END and most of GET is given; others require your code
    
        # python 3.10 adds switch statements, but lectura has python 3.8
        if tag == END:
            if myRank == 0:
                headEnd()
            else:
                storageEnd()
            pass
        elif tag == ADD:
            addNode(source)
        elif tag == REMOVE:
            removeNode(source)
        elif tag == PUT:
            putKeyVal(source)
        elif tag == GET:
            getKeyVal(source)
        elif tag == ACK:
            # Receieve ACK tag
            response = MPI.COMM_WORLD.recv(source=source, tag=ACK)

            # If back to head, send to command node
            if myRank == 0:
                MPI.COMM_WORLD.send(response, dest=numProcesses-1, tag=ACK)

            else:
                # Move toward head node
                MPI.COMM_WORLD.send(response, dest=childRank, tag=ACK)
        elif tag == RETVAL:
            # Receieve RETVAL tag aka value and storage id
            response = MPI.COMM_WORLD.recv(source=source, tag=RETVAL)

            # If back to head, send to command node
            if myRank == 0:
                MPI.COMM_WORLD.send(response, dest=numProcesses-1, tag=RETVAL)

            else:
                # Move toward head node
                MPI.COMM_WORLD.send(response, dest=childRank, tag=RETVAL)
        # NOTE: you probably will want to add more cases here, e.g., to handle data redistribution
        elif tag == ADD_NODE:
            triggerNode(source)
        elif tag == REMOVE_NODE:
            removeCurrNode(source)
        else:
            # should never be reached---if it is, something is wrong with your code; just bail out
            print(f"ERROR, my id is {myRank}, source is{source}, tag is {tag}")
            sys.exit(1)

if __name__ == "__main__":
    
    # get my rank and the total number of processes
    numProcesses = MPI.COMM_WORLD.Get_size()
    myRank = MPI.COMM_WORLD.Get_rank()

    table = {}

    # set up the head node and the last storage node
    if myRank == 0:
        myStorageId = 0
        childRank = numProcesses - 2
        childStorageId = MAX

    elif myRank == numProcesses - 2:
        myStorageId = MAX
        childRank = 0
        childStorageId = 0

    else:
        myStorageId = None
        childRank = None
        childStorageId = None

    # the command node is handled separately
    if myRank < numProcesses-1:
        handleMessages()
    else:
        commandNode()
    
