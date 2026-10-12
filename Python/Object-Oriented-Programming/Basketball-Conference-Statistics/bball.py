'''
    bball.py
    Eric Romero
    Purpose: Takes a text file with teams, conferences, wins, and losses.
    It will process to find the win ratio for each team and return the high 
    average win ratio of these conferences overall.
    CSc 120, Section 1, TA: Marissa
'''
class Team:
    '''
    This class represents the team belonging to a conference

    The object takes a text line and creates attributes for name, conference, 
    and win ratio.

    Methods defined are getter methods for the attributes.
    Special methods defined are the init and string methods.
    '''
    def __init__(self, line):
        '''
        Takes in a text line and splits into respective attributes 
        depending on the index within the list it forms. 
            
        Parameters:
            line = string text line with data for 
            team, conference, wins, and losses
        '''
        self._line = line.strip('\n').split()
        index1 = 0
        index2 = 0
        # Find position of conference parenthesis to find name and conference
        for i in range(len(self._line)):
            if '(' in self._line[i]:
                index1 = i
            if ')' in self._line[i]:
                index2 = i
        name = ' '.join(self._line[0 : index1])
        conference = ' '.join(self._line[index1 : index2 + 1])
        loss = self._line[-1]
        wins = self._line[-2]
        win_ratio = int(wins) / (int(wins) + int(loss))
        self._name = name
        # Returns conference without parenthesis
        self._conf =  conference[1:-1]
        self._win_ratio = win_ratio

    def name(self):
        return self._name
    
    def conf(self):
        return self._conf
    
    def win_ratio(self):
        return self._win_ratio

    def __str__(self):
        return '{} : {}'.format(str(self._name), str(self._win_ratio))

class Conference:
    '''
        This class represents a Conference, a collection of teams.

        Every conference class object is a set that holds a Team 
        class object within a list.

        This class defines special methods for init, contains to see 
        if a team exists in the team list for this conference, and string. 
        Normal methods defined are name(to return the name of the conference)
        , add, and win_ratio_avg()
    '''
    def __init__(self, conf):
        '''
        Takes a conference name and creates an empty list for the teams for this conference.

        Parameters:
            conf: A string for the name of the conference 
        '''
        self._conf_name = conf
        self._team_list = []

    def __contains__(self, team):
        '''
        Checks to see if team exists within team list of conferences

        Parameters:
            team: Team class object that exists within team list. 
        '''
        return team in self._team_list
    
    def name(self):
        return self._conf_name
    
    def add(self, team):
        self._team_list.append(team)

    def win_ratio_avg(self):
        '''
        Finds the win ratio average by adding all the win ratios and 
        divides by the length of the team list. 
        '''
        total = 0
        for team in self._team_list:
            total += team.win_ratio()
        result = total / int(len(self._team_list))
        return result

    def __str__(self):
        win_ratio_str = str(self.win_ratio_avg())
        return '{} : {}'.format(str(self._conf_name), win_ratio_str)

class ConferenceSet:
    '''
    This class represents a set for conferences to be collected into.
    Every conference hold a Team class object.

    This class is built on an initially empty list, which will be filled 
    with conferences, just as every conference is filled with teams.

    Special method for this class is init, but the normal methods 
    defined are add(adding to the conference list) and 
    best(finds the highest win ratio)
    '''
    def __init__(self):
        '''
        '''
        self._conf_list = []

    def add(self, team):
        '''
        Depending on the team:
            1. A Conference class object is created if teams 
            conference not in conference list.
            2. Team added to conference class object if exists.
        '''
        check = True
        conf = team.conf()
        # Process 2
        for conference in self._conf_list:
            if conf == conference.name():
                conference.add(team)
                check = False
        # Process 1
        if check:
            new_conf = Conference(conf)
            new_conf.add(team)
            self._conf_list.append(new_conf)

    def best(self):
        '''
        This method will process the conference list:
            1. Find the highest win average
            2. Retrieve a list of conferences with that high average.
        Returns:
            List with conferences with highest win ratio average.
        '''
        conf_high_avg = []
        avg = 0
        # Process 1
        for conf in self._conf_list:
            if conf.win_ratio_avg() > avg:
                avg = conf.win_ratio_avg()
        # Process 2
        for conf in self._conf_list:
            if conf.win_ratio_avg() == avg:
                conf_high_avg.append(conf)
        return conf_high_avg

def process_file(file, conference_set):
    '''
    This function will 
        1. intiilizaes Team class objects based off text line
        2. Outputs a sorted list of conferences and best win ratio average.
    Parameters:
        file: String text file containing womens teams, conferences, wins, and losses
        conference_set: Initially empty ConferenceSet class object to store conferences. 
    '''
    for line in file:
        if line[0] == '#':
            continue
        team = Team(line)
        conference_set.add(team)
    conference_list1 = conference_set.best()
    conference_list2 = []
    # Elements in conference_list1 are references to location in memory 
    # This will convert into a python list to be properly sorted. 
    for conf in conference_list1:
        name = conf.name()
        avg = conf.win_ratio_avg()
        conference_list2.append('{} : {}'.format(name, avg))
    output = sorted(conference_list2)
    for pair in output:
        print(pair)

def main():
    file = input()
    open_file = open(file, 'r')
    conference_set = ConferenceSet()
    process_file(open_file, conference_set)
    
main()