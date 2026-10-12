'''
    dates.py
    Eric Romero
    Purpose:
    Reads in a file to manage a datatbase of dates and events and 
    recogmize dates in different formats.
    Afterwards, will print out events assicated with that date 
    CSc 120, Section 001, Fall 2023
'''

class Date:
    '''
    Represents a date with associated events

    Constructs date objects with a date and initial event

    Has methods for setters and getters.
    '''
    def __init__(self, date, event):
        '''
        Initializes a Date object

        Arguments:
            date (str): The date in string format
            event (str): The initial event associated with the date
        '''
        self._date = date
        self._events = [event]
    
    # Setters
    def add_event(self, event):
        '''
        Adds events to list of events associated with the Date objects

        Argument:
            event(str): Event to add
        '''
        self._events.append(event)

    # Getters
    def get_date(self):
        return self._date
    
    def get_events(self):
        return self._events
    
    # Special Methods
    def __str__(self):
        return "{}: {}".format(str(self._date), str(self._events))
    
    def __repr__(self):
        return str(self._events)
    
class DateSet:
    '''
    Represents a collection of dates and their associated events

    Constructs a DateSet object with no arguments

    Has methods for setters and getters
    '''
    def __init__(self):
        '''
        Initializes a DateSet object with an empty dictionary to store 
        Date objects
        '''
        self._dateDict = {}

    def add_date(self, date, event):

        if date in self._dateDict:
            self._dateDict[date].add_event(event)
        else:
            self._dateDict[date] = Date(date, event)

    def get_dateDict(self):
        return self._dateDict
    
    def __str__(self):
        return str(self._dateDict)


def canonicalize_date(date_str):
    '''
    Canonicalizes date format to YYYY-MM-DD

    Argument:
        date_str (str): The input date in various formats

    Returns:
        str: Canonicalized date in the format YYYY-MM-DD
    '''
    # YYYY-MM-DD
    if len(date_str.split('-')) == 3:
        d = date_str.split('-')
        yyyy = d[0]
        mm = d[1]
        dd = d[2]

    # DD/MM/YYYY
    elif len(date_str.split('/')) == 3:
        d = date_str.split('/')
        mm = d[0]
        dd = d[1]
        yyyy = d[2]

    # MON DD YYYY
    elif len(date_str.split(' ')) == 3:
        d = date_str.split(' ')

        month = [' ', 'Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', \
                 'Aug', 'Sep', 'Oct', 'Nov', 'Dec']
        yyyy = d[2]
        dd = d[1]
        mm = month.index(d[0])
    return "{:d}-{:d}-{:d}".format( int(yyyy ), int(mm), int(dd))

def processing_file(fname, date_set):
    '''
    Processes lines from file, performs date and event operation

    Arguments:
        fname(file): An open file object containing date and event operations.
        date_set (DateSet): A DateDet to store and manage dates and event
    '''
    for line in fname:

        line = line.strip('\n').split(':')
   
        if 'I ' in line[0]:
            # Starts with "I": insert operation
            date = line[0].strip('I ')
            date = date.strip(' ')
            # Canonicalizes date format
            append_date = canonicalize_date(date)
            # Extracts event from remaining part of line
            event = ':'.join(line[1:])
            event = event.strip()
            # Adds date and event to DateSet
            date_set.add_date(append_date, event)

        elif 'R ' in line[0]:
            # Starts with 'R': retrieval operation
            date = line[0].strip('R ')
            date = date.strip(' ')
            # Canonicalize date format
            canon_date = canonicalize_date(date)
            # Retrieves events for specific date and print
            dates_dict = date_set._dateDict
            if canon_date in dates_dict:
                values = dates_dict[canon_date]
                events = values.get_events()
                events.sort()
                for event in events:
                    print('{}: {}'.format(canon_date, event))
        else:
            # If line doesn't match 'R' or 'I', prints error message
            print('Error - Illegal operation.')


def main():
    filename = input()
    fname = open(filename)
    date_set = DateSet()
    processing_file(fname, date_set)
    fname.close()
main()