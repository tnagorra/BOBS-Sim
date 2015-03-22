@8000 ; Main loop at 8000
EI
NOP
JMP 8001

@003C ; Interrupt 7.5 at 003C
JMP 7500

@0034 ; Interrupt 6.5 at 0034
JMP 7000

@9000 ; Data at 9000
0A 00 0F 01 02 07 03 05 07 06

@7000 ; Bubble sorting Descending at 7000
MVI E,0Ah
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
MVI B, 0AH
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
