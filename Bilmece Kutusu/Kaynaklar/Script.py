"""

This code gets a new bilmece
and converts it to proper Java code

"""


fileName = "bilmeceler.txt"

with open (fileName,"r") as file:
    all_data =file.readlines()


newData = []


for M in all_data:
	if(M == "\n"):
		continue
	else:
		M = M.strip()
		newData.append(M)

# CIFT -> SORU
# TEK -> CEVAP

file = open("code.txt","w+")

counter = 0
same = True
while(counter < len(newData)):
	if(counter % 2 == 0):
		file.write("iterator = new Bilmece();" + "\n" )
		file.write("iterator.setSoru(" + "\"" + newData[counter] + "\""+ ");" + "\n")
		counter += 1
		continue
	else:
		file.write("iterator.setYanit(" + "\"" + newData[counter] + "\""+ ");" + "\n")
		file.write("bilmeceler.add(iterator);" + "\n")
		file.write("\n")
		counter += 1
		continue
