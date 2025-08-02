strictfp  class srfloat {
    public static void main(String[] args) {

            // floating-point calculations will be strictfp
        float x= 5.55F;
        float y= 5.5F;
        System.out.println(x*y);

       /* Used to restrict floating-point calculations to ensure that they adhere strictly
        to the IEEE 754 standard, which can result in consistent results across platforms.
                It can be applied to classes, interfaces, or methods.

               - Without strictfp (platform-dependent precision)
               - With strictfp (consistent precision across platform)
                */
        System.out.println("widening casting");
        //widening casting
        int a=5;
        long l=a;
        float b=a;
        double c=a;
        System.out.println(a);
        System.out.println(l);
        System.out.println(b);
        System.out.println(c);
        System.out.println("arrowing casting");
        //narrowing casting
        double g=5.16;
        long h=(long)g;
        float i=(float)g;
        char j=(char)g;//number can't be changed to character or letter
        System.out.println(g);
        System.out.println(h);
        System.out.println(i);
        System.out.println(j);



    }
}
