@8000 ; Main loop at 8000
EI
NOP
JMP 8001

@003C ; Interrupt 7.5 at 003C
JMP 7500

@0034 ; Interrupt 6.5 at 0034
JMP 7000

@9000 ; Data at 9000
b5 da 72 de 64 5d b1 ec 3a 43 2a ce 9c c0 a6 03 d5 27 ef 4f 81 cc ff ae 48 95 13 b4 62 f1 d8 52 c6 bc 09 83 88 60 9d 7e 7c 40 1f 0b 59 b9 76 8f 30 71 ca 9f d1 89 99 0e c4 75 14 65 6d 2f 25 16 87 33 2c 56 a2 aa fc e5 00 1d dd 4b 5a 22 e1 0a d9 3c 37 0c 51 06 92 1a 32 69 0f 79 e9 e0 49 4c f4 f8 46 8c

@7000 ; Bubble sorting Descending at 7000
MVI E,64h
MOV D,E             ; 7002
LXI H,9000h
MOV A,M
MOV C,A             ; 7007
INX H               ; 7008
DCR D
JZ 7019
MOV A,M
CMP C
JC 7007
MOV M,C
DCX H
MOV M,A
INX H
JMP 7008
DCR E               ; 7019
JNZ 7002
JMP 8000            ; Halt


@7500 ; Selection Sorting Ascending at 7500
LXI H, 9000H
MVI B, 64H
MOV C, B            ;7505
MOV D, H
MOV E, L
INR E
LDAX D              ; 7509
CMP M
JNC 7513
MOV D, M            ;750E
MOV M, A
MOV A, D
MOV D, H
STAX D
INR E               ; 7513
DCR C
JNZ 7509
INX H
DCR B
JNZ 7505
JMP 8000            ; Halt
