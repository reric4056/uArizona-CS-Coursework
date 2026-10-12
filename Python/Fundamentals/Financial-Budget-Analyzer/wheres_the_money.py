###
### Eric Romero
### CSc 110
### Benjamin Dicken
### Description: Opens with a welcome message. Accepts income and expense inputs. Uses given inputs
### to visualize the financial situation.

### TITLE CARD
print('-----------------------------')
print("----- WHERE'S THE MONEY -----")
print('-----------------------------')
### Input expressions & isNumeric Check
salary = input('What is your annual salary?\n')
red = salary.isnumeric()
if red != True:
    print('Must enter positive integer for salary.')
    exit()

rent = input('How much is your monthly mortgage or rent?\n')
green = rent.isnumeric()
if green != True:
    print('Must enter positive integer for mortgage or rent.')
    exit()

bills = input('What do you spend on bills monthly?\n')
blue = bills.isnumeric()
if blue != True:
    print('Must enter positive integer for bills.')
    exit()

food = input('What are your weekly grocery/food expenses?\n')
yellow = food.isnumeric()
if yellow != True:
    print('Must enter positive integer for food.')
    exit()

travel = input('How much do you spend on travel annually?\n')
orange = travel.isnumeric()
if orange != True:
    print('Must enter positive integer for travel.')
    exit()

print()
### Convert from STRING to INTEGER
salary = int(salary)
rent = int(rent)
bills = int(bills)
food = int(food)
travel = int(travel)

### Conversion of monthly to annual expenses for RENT & BILLS
rent = rent * 12
bills = bills * 12
### Conversion of weekly to annual expenses for FOOD
food = food * 52
### Conversion for tax amounts
if 0 <= salary <= 15000:
    taxes = salary * 0.1
elif 15000 < salary <= 75000:
    taxes = salary * 0.2
elif 75000 < salary <= 200000:
    taxes = salary * 0.25
else:
    taxes = salary * 0.3
### Extra value
extra = salary - rent - bills - food - travel - taxes
### Conversion for Percentage
rentPercent = (rent / salary) * 100
billPercent = (bills / salary) * 100
foodPercent = (food / salary) * 100
travelPercent = (travel / salary) * 100
taxPercent = (taxes / salary) * 100
extraPercent = 100.00 - rentPercent - billPercent - foodPercent - travelPercent - taxPercent
### Formatting Total Values
rent_print = format(rent, '11,.2f')
bill_print = format(bills, '11,.2f')
food_print = format(food, '11,.2f')
travel_print = format(travel, '11,.2f')
tax_print = format(taxes, '11,.2f')
extra_print = format(extra, '11,.2f')
### Formatting Percentage Values
rent_percent_print = format(rentPercent, '5,.1f')
bill_percent_print = format(billPercent, '5,.1f')
food_percent_print = format(foodPercent, '5,.1f')
travel_percent_print = format(travelPercent, '5,.1f')
tax_percent_print = format(taxPercent, '5,.1f')
extra_percent_print = format(extraPercent, '5,.1f')

### Hashtag counts
rent_hashtag = '#' * round(int(rentPercent), 2)
bill_hashtag = '#' * round(int(billPercent), 2)
food_hashtag = '#' * round(int(foodPercent), 2)
travel_hashtag = '#' * round(int(travelPercent), 2)
tax_hashtag = '#' * round(int(taxPercent), 2)
extra_hashtag = '#' * round(int(extraPercent), 2)
### Dash printing
dash = max(round(int(rentPercent)), round(int(billPercent)), round(int(foodPercent)), round(int(travelPercent)), round(int(taxPercent)), round(int(extraPercent)))
print('-' * dash + '-' * 42)
print('See the financial breakdown below, based on a salary of $' + str(salary))
print('-' * dash + '-' * 42)
print('| mortgage/rent | $' + str(rent_print), '| ' + str(rent_percent_print) + '% | ' + str(rent_hashtag))
print('|         bills | $' + str(bill_print), '| ' + str(bill_percent_print) + '% | ' + str(bill_hashtag))
print('|          food | $' + str(food_print), '| ' + str(food_percent_print) + '% | ' + str(food_hashtag))
print('|        travel | $' + str(travel_print), '| ' + str(travel_percent_print) + '% | ' + str(travel_hashtag))
print('|           tax | $' + str(tax_print), '| ' + str(tax_percent_print) + '% | ' + str(tax_hashtag))
print('|         extra | $' + str(extra_print), '| ' + str(extra_percent_print) + '% | ' + str(extra_hashtag))
print('-' * dash + '-' * 42)

if taxes > 75000:
    print('>>> TAX LIMIT REACHED <<<')
if extra < 0:
    print('>>> WARNING: DEFICIT <<<')
    exit()
else:
    exit()
