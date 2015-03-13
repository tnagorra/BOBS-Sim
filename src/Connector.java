class Connector {

    static public void connect(Microprocessor upro, Memory mem){
        upro.memory = mem;
        mem.up = upro;
    }

}
