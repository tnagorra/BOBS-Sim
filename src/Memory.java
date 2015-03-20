class Memory  extends Thread {

    Microprocessor up;

    Register8[] arr;

    public Memory(int size) {
        arr= new Register8[size];
        for(int i=0; i<arr.length; i++)
            arr[i] = new Register8(0);
    }

    public void load( Register16 position, int[] opcode ) {
        for(int i=0; i < opcode.length && i+position.get() < arr.length; i++) {
            arr[i+position.get()] = new Register8(opcode[i]);
        }
    }

    public void print( Register16 position, int total) {
        System.out.print( position.hex()+ " : ");
        for(int i=0; i < total && i+position.get() < arr.length; i++) {
            System.out.print(arr[i+position.get()].hex()+" ");
        }
        System.out.print("\n");
    }

    private Register8 get(Register16 position) {
        if( position.get() >=arr.length || position.get() <0 )
            throw new IndexOutOfBoundsException();
        // Shouldn't return object
        return arr[position.get()].clone();
    }

    private void set(Register16 position, Register8 reg) {
        if( position.get() >=arr.length || position.get() <0 )
            throw new IndexOutOfBoundsException();
        // Shouldn't copy object
        arr[position.get()].copy(reg);
    }


    public void run() {
        try {
            synchronized(up) {
                System.out.println("Memory started!");
                while( true  ) {
                    // Wait for some signal
                    up.wait();
                    if( up.iom == false) {
                        if ( up.write == true ) {
                            Register16 taddress = new Register16(up.busL, up.busH);
                            up.notify();
                            up.wait();
                            Register8 tdata = up.busL.clone();
                            set(taddress,tdata);
                            up.notify();
                        } else if ( up.read == true ) {
                            Register16 taddress = new Register16(up.busL, up.busH);
                            up.busL = get(taddress);
                            up.notify();
                        } else {
                            if( up.read && up.write) {
                                System.out.print("This is read/write signal error. ");
                            }
                            // if both of them are false then this may occur
                            // when up.wait() was forced to stop
                        }
                    }
                }
            }
        } catch (InterruptedException i) {
            System.out.println("Memory released!");
        }
    }
}

