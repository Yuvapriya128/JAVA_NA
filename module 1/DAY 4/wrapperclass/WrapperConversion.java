package WrapperClass;
/*
double → Double	✅ Autoboxing
Double → double	✅ Auto-unboxing
null Integer → int	⚠️ Auto-unboxing + NPE
    */
public class WrapperConversion {
    public static void main(String[] args) {
        int intval=8;

//        Boxing
        Integer integer=Integer.valueOf(intval);
        System.out.println(integer);

//        Autoboxing
        Integer autointeger=intval;
        System.out.println(autointeger);

//        Unboxing
        int unboxinteger=integer.intValue();
        System.out.println(unboxinteger);

        WrapperFloat();
        WrapperBoolean();
        WrapperByte();
        WrapperDouble();
        WrapperChar();
        WrapperShort();
        WrapperLong();
    }
    public static void WrapperFloat(){
        float primfloat =8f;

        Float objfloat=Float.valueOf(primfloat);
        Float autoobjfloat=primfloat;

        float unboxfloat=objfloat.floatValue();

        System.out.println(unboxfloat);
        System.out.println(objfloat);
        System.out.println(autoobjfloat);
    }
    public   static void WrapperDouble(){
        double primdouble = 1.4;

        Double objdouble=Double.valueOf(primdouble);
        Double autoobjdouble=primdouble;

        double unboxdouble=objdouble.doubleValue();

        System.out.println(unboxdouble);
        System.out.println(objdouble);
        System.out.println(autoobjdouble);
    }
    public static void WrapperLong(){
        long primlong=8L;

        Long objlong=Long.valueOf(primlong);
        Long autoobjlong=primlong;

        long unboxlong=objlong.longValue();

        System.out.println(objlong);
        System.out.println(autoobjlong);
        System.out.println(unboxlong);
    }
    public static void WrapperShort(){
        short primshort=1;

        Short objshort=Short.valueOf(primshort);
        Short autoshort=primshort;

        short unshort=objshort.shortValue();

        System.out.println(objshort);
        System.out.println(autoshort);
        System.out.println(unshort);
    }
    public  static void WrapperByte(){
        byte primbyte=10;

        Byte objbyte=Byte.valueOf(primbyte);
        Byte autobyte=primbyte;

        byte unbyte=objbyte.byteValue();

        System.out.println(objbyte);
        System.out.println(autobyte);
        System.out.println(unbyte);



    }
    public static void WrapperBoolean(){
        boolean flag=true;

        Boolean objflag=Boolean.valueOf(flag);
        Boolean autoflag=flag;

        boolean unflag=objflag.booleanValue();

        System.out.println(unflag+" "+objflag+" "+autoflag);

    }
    public static void WrapperChar(){
        char ch='y';

        Character objchar=Character.valueOf(ch);
        Character autoobj=ch;

        char unch=objchar.charValue();
        System.out.println(objchar+" "+autoobj+" "+unch);
    }
}

