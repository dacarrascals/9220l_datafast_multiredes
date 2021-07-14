package com.datafast.server;

public class Contador {
    private int cont = 0;

    synchronized public int getCount(){
        try {
            cont++;
            if (cont>99)
                cont = 0;
        }catch (Exception e){
        }
        return cont;
    }
}
