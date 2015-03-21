class Ppi extends Thread {
    Microprocessor up;
    Register8 baseAddress;
    Register8[] ports;

    public Ppi(Register8 baseAddr){
        baseAddress = baseAddr.clone();
        ports = new Register8[4];
        for(int i = 0; i < ports.length; i++)
            ports[i] = new Register8(0);
    }

    public void initializePPI(Register8 cw) {
        ports[3].copy(cw);
    }

    private int ReadPort(int index) {
        int data = 0x0;
        if(index == 2) {
            if(ports[2].get(4))
                data |= ports[2].get() & 0xf0;
            else if(ports[2].get(0))
                data |= ports[2].get() & 0x0f;
        }
        else
            data = ports[index].get();
        return data;
    }

    public Register8 Read(Register8 position) {
        int index = position.get() - baseAddress.get();
        if(index >= ports.length || index < 0)
            throw new IndexOutOfBoundsException();
        int value = ReadPort(index);
        return new Register8(value);
    }

    private void  WritePort(int index , int value) {
        if(index == 3) {
            ports[3].set(value);
            if(ports[3].get(7) == false){
                int BitPos = ports[3].get(3,1);
                boolean  val = ports[3].get(0);
                ports[2].set(BitPos, val);
            }
        }
        else if(index == 2) {
            if(ports[3].get(4) == false) {
                int data = (value & 0xf0) | (value & 0x0f);
                ports[2].set(data);
            }
            if(ports[3].get(0) == false) {
                int data = (value & 0xf0) | (value & 0x0f);
                ports[2].set(data);
            }
        }
        else
            ports[index].set(value);
    }

    public void  Write(Register8 position, Register8 reg) {
        int index = position.get() - baseAddress.get();
        if(index >= ports.length || index < 0)
            throw new IndexOutOfBoundsException();
        int value = reg.get();
        WritePort(index ,value);
    }
    private  void print() {

    }
    public void run() {
        try {
            synchronized(up) {
                while(!up.active)
                    Thread.sleep(1);
                while(up.active) {
                    up.wait();
                    if(up.iom == true) {
                        if(up.write == true) {
                            Register8 taddress = up.busL.clone();
                            up.notify();
                            up.wait();
                            Register8 tdata = up.busL.clone();
                            Write(taddress, tdata);
                            up.notify();
                        }
                        else if(up.read == true) {
                            Register8 taddress = up.busL.clone();
                            up.busL = Read(taddress);
                            up.notify();
                        }
                        else {
                            if(up.read && up.write) {
                                System.out.print("This is read / write signal error.");
                            }
                        }
                    }
                }
            }
        } catch(InterruptedException i){}
    }
}
