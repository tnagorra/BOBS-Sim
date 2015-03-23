@8000 ; Main loop at 8000
EI
NOP
JMP 8001

@003C ; Interrupt 7.5 at 003C
CALL 8300
RET

@0034 ; Interrupt 6.5 at 0034
CALL 8200
RET

@9000 ; Data at 9000
b5 da 72 de 64 5d b1 ec 3a 43 2a ce 9c c0 a6 03 d5 27 ef 4f 81 cc ff ae 48 95 13 b4 62 f1 d8 52 c6 bc 09 83 88 60 9d 7e 7c 40 1f 0b 59 b9 76 8f 30 71 ca 9f d1 89 99 0e c4 75 14 65 6d 2f 25 16 87 33 2c 56 a2 aa fc e5 00 1d dd 4b 5a 22 e1 0a d9 3c 37 0c 51 06 92 1a 32 69 0f 79 e9 e0 49 4c f4 f8 46 8c


@8200 ; Bubble sorting Descending at 8200
MVI E,64h
MOV D,E             ; 8202
LXI H,9000h
MOV A,M
MOV C,A             ; 8207
INX H               ; 8208
DCR D
JZ 8219
MOV A,M
CMP C
JC 8207
MOV M,C
DCX H
MOV M,A
INX H
JMP 8208
DCR E               ; 8219
JNZ 8202
RET

@8300 ; Selection Sorting Ascending at 8300
LXI H, 9000H
MVI B, 64H
MOV C, B            ;8305
MOV D, H
MOV E, L
INR E
LDAX D              ; 8309
CMP M
JNC 8313
MOV D, M            ;830E
MOV M, A
MOV A, D
MOV D, H
STAX D
INR E               ; 8313
DCR C
JNZ 8309
INX H
DCR B
JNZ 8305
RET
