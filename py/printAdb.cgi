#!/usr/bin/python3
import sqlite3
import time
import sys

if(len(sys.argv) > 1):
    db = sqlite3.connect(sys.argv[1])
else:
    db = sqlite3.connect("/home/maxx/Airport/__airport.db")
c = db.cursor()

print("Content-Type: text/html\n\n<pre>")

print("Flights:")
c.execute("SELECT * FROM flight")
print("|--ID--|--------From--------|---------To---------|------Date------|")
for i in c.fetchall():
    print("|%6s|%20s|%20s|%16s|" % (i[0], i[1], i[2], i[3]))

print("\nUsers:")
c.execute("SELECT * FROM user")
print("|--ID--|------Username------|--------------------------Password--------------------------|--Refresh--|Refresh Date(+3)|")
for i in c.fetchall():
    print("|%6s|%20s|%60s|%11s|%16s|" % (i[0], i[1], i[2], i[3], time.strftime("%Y-%m-%d %H:%M", time.gmtime((0 if i[4]=='' else int(i[4]))/1000))))

print("\nFavs:")
c.execute("SELECT * FROM favs")
print("|--USER ID--|--FLIGHT ID--|")
for i in c.fetchall():
    print("|%11s|%13s|" % (i[0], i[1]))
print("</pre>")
db.close()
