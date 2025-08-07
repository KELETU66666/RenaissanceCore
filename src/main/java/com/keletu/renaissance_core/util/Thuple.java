package com.keletu.renaissance_core.util;

public class Thuple<A, B, C>
{
    private A a;
    private B b;
    private C c;

    public Thuple(A aIn, B bIn, C cIn)
    {
        this.a = aIn;
        this.b = bIn;
        this.c = cIn;
    }

    /**
     * Get the first Object in the Tuple
     */
    public A getFirst()
    {
        return this.a;
    }

    /**
     * Get the second Object in the Tuple
     */
    public B getSecond()
    {
        return this.b;
    }

    /**
     * Get the third Object in the Tuple
     */
    public C getThird()
    {
        return this.c;
    }
}