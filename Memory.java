class Memory {
    Register8[] arr;

    public Memory(int size){
         arr= new Register8[size];
        for(int i=0;i<arr.length;i++)
            arr[i] = new Register8(i);
    }

    public Register8 get(Register16 position) {
        if( position.get() >=arr.length || position.get() <0 )
            throw new IndexOutOfBoundsException();
        // Shouldn't return object
        return arr[position.get()].clone();
    }

    public void set(Register16 position, Register8 reg) {
        if( position.get() >=arr.length || position.get() <0 )
            throw new IndexOutOfBoundsException();
        // Shouldn't copy object
        arr[position.get()] = reg.clone();
    }

}
