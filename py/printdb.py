#!/usr/bin/python3
import sqlite3

db = sqlite3.connect("__airport.db")
c = db.cursor()


print("Flights:")
c.execute("SELECT * FROM flight")
print("|--ID--|--------From--------|---------To---------|------Date------|")
for i in c.fetchall():
    print("|%6s|%20s|%20s|%16s|" % (i[0], i[1], i[2], i[3]))

print("\nUsers:")
c.execute("SELECT * FROM user")
print("|--ID--|------Username------|--------------------------Password--------------------------|--Refresh--|--Refresh Date--|")
for i in c.fetchall():
    print("|%6s|%20s|%60s|%11s|%16s|" % (i[0], i[1], i[2], i[3], i[4]))

print("\nFavs:")
c.execute("SELECT * FROM favs")
print("|--USER ID--|--FLIGHT ID--|")
for i in c.fetchall():
    print("|%11s|%13s|" % (i[0], i[1]))

db.commit()
db.close()