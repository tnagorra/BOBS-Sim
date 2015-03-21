LDA 9000
MOV C, A        ;"Initialize counter"
MVI B, 00       ;"sum = 0"
LXI H, 9001     ;"Initialize pointer"
MOV A, M        ;"Get the number" BACK 8009
ANI 01          ;"Mask Bit 1 to Bit7"
JZ 8012         ;"Don’t add if number is ODD"
MOV A, B        ;"Get the sum"
ADD M           ;"SUM = SUM + data"
MOV B, A        ;"Store result in B register"
INX H           ;"increment pointer"  SKIP 8012
DCR C           ;"Decrement counter"
JNZ 8009        ;"if counter 0 repeat"
MOV A,B
STA 9000H       ;"store sum"
HLT             ;"Terminate program execution"
