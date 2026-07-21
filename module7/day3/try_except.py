



try:
    a=int(input("Enter a:"))
    b=int(input("Enter b:"))
    res=a/b
    print(res)
except(ZeroDivisionError):
    print(" b can't be zero so error")
except(ValueError):
    print("error")
else:
    print("result is:",res)
finally:
    print("this is finally block-will execute always(error or not)")


# custom exception
class NegativeBalance(Exception):
    pass

amt=-200
try:
    if amt<0:
        raise NegativeBalance("Balance can't be negative")
except NegativeBalance as err:
    print(err)
