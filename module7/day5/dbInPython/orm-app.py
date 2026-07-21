from sqlalchemy import create_engine, Column, Integer, String, Float, Date, ForeignKey
from sqlalchemy.orm import declarative_base
from sqlalchemy.orm import relationship, sessionmaker

# step 1: Create a database connection
engine = create_engine('postgresql://postgres:12345@localhost:5432/northernarc')

# step 2:define schema using declarative_base
Base = declarative_base()

class Scan(Base):
    __tablename__ = 'scans'

    id = Column(Integer, primary_key=True)
    name = Column(String(255))
    brokenlinks = Column(Integer)

    def __init__(self, name, brokenlinks):
        self.name = name
        self.brokenlinks = brokenlinks

# formatting the output of the object when printed
    def __repr__(self):
        return f"Scan(id={self.id}, name='{self.name}', brokenlinks={self.brokenlinks})"

# step 3: Fetch the data from the database using sessionmaker
Session = sessionmaker(bind=engine)
session = Session()

# create the table in the database before inserting data
Base.metadata.create_all(engine)
print("Table created successfully")

session.add_all([
    Scan(name='Scan A', brokenlinks=10),
    Scan(name='Scan B', brokenlinks=19),
])

session.add(Scan(name='Scan C', brokenlinks=29))

session.commit()

# or use Scan class to do insertions in db
scan_d = Scan(name='Scan D', brokenlinks=15)
session.add(scan_d)

# insert multiple records using Scan class

session.add_all([
    scan_d,
    Scan(name='Scan E', brokenlinks=25),
    Scan(name='Scan F', brokenlinks=35),
    Scan(name='Scan G', brokenlinks=45)
])

session.commit()

# # step 4:create metadata and create the table in the database
# Base.metadata.create_all(engine)
# print("Table created successfully")


# step 5: Query the data
print("Querying scan")
scans = session.query(Scan).all()
for scan in scans:
    print(scan, '\n')

# querying a specific record using filter_by
#  both filter and filter_by can be used to query the data. The difference is that filter_by uses keyword arguments, while filter uses expressions.

# print("Querying scan with name 'Scan A'")
# scan_a = session.query(Scan).filter(Scan.name=='Scan A').all()
# print(scan_a)

print("Querying scan with name 'Scan A'")
scan_a = session.query(Scan).filter_by(name='Scan A', brokenlinks=10).all()
print(scan_a)


# querying a specific record using filter
print("Querying only scan names")
scan_names = session.query(Scan.name).all()
for name in scan_names:
    print(name)

# updating a record: is there after filtering the record, we can update the record by changing the attribute values and committing the changes to the database using update({name: 'Scan A Updated'}) or by changing the attribute values and committing the changes to the database using session.commit().
# give example of both methods

print("Update")
existing_scan = session.query(Scan).filter_by(id=1).first()
print("Existing scan before update:", existing_scan)
existing_scan.name = 'Scan A Updated'
session.commit()
print("Existing scan after update:", existing_scan)

print("Update using update() method")
session.query(Scan).filter_by(id=2).update({Scan.name: 'Scan B Updated'})
session.commit()

print("Delete")
scan_to_delete = session.query(Scan).filter_by(id=1).first()
if scan_to_delete:
    session.delete(scan_to_delete)
    session.commit()
    print("Deleted scan:", scan_to_delete)

