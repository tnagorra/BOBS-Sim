class Memory  extends Thread {

    Microprocessor up;
    Register8[] memories;

    // Get if the address belong to this device
    private boolean isMine(Register16 addr) {
        int index = addr.get();
        if ( index >= memories.length || index < 0 )
            return false;
        return true;
    }

    // Memory Constructor
    public Memory(int size) {
        memories = new Register8[size];
        for (int i = 0; i < memories.length; i++)
            memories[i] = new Register8(0);
    }

    // Load data using integer array into memory
    public void load( Register16 position, int[] opcode ) {
        for (int i = 0; i < opcode.length && i + position.get() < memories.length; i++) {
            memories[i + position.get()] = new Register8(opcode[i]);
        }
    }

    // Get data from address 'position' of memory
    public Register8 get(Register16 position) {
        if( !isMine(position))
            throw new IndexOutOfBoundsException();
        // Shouldn't return object
        return memories[position.get()].clone();
    }

    // Set data from 'reg' to address 'position' of memory
    private void set(Register16 position, Register8 reg) {
        if( !isMine(position))
            throw new IndexOutOfBoundsException();
        // Shouldn't copy object
        memories[position.get()].copy(reg);
    }

    // Main running loop
    public void run() {
        try {
            synchronized (up) {
                System.out.println("Memory started!");
                while (true ) {
                    // Wait for some signal
                    up.wait();
                    if ( !(up.iom == false) )
                        continue;

                    if ( up.write ) {
                        Register16 taddress = new Register16(up.busL, up.busH);
                        // Shouldn't notify if address doesn't belong
                        // to the io device
                        if (!isMine(taddress))
                            continue;
                        up.notify();
                        up.wait();
                        Register8 tdata = up.busL.clone();
                        set(taddress, tdata);
                        up.notify();
                    } else if ( up.read ) {
                        Register16 taddress = new Register16(up.busL, up.busH);
                        // Shouldn't notify if address doesn't belong
                        // to the io device
                        if (!isMine(taddress))
                            continue;
                        up.busL = get(taddress);
                        up.notify();
                    } else {
                        if ( up.read && up.write)
                            System.out.print("R/W signal error. ");
                        // if both of them are false then this may occur
                        // when forced to stop
                    }
                }
            }
        } catch (InterruptedException i) {
            System.out.println("Memory released!");
        }
    }

    /*
    public void print( Register16 position, int total) {
        System.out.print( position.hex() + " : ");
        for (int i = 0; i < total && i + position.get() < memories.length; i++) {
            System.out.print(memories[i + position.get()].hex() + " ");
        }
        System.out.print("\n");
    }
    */

}






