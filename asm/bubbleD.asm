; Bubble sorting @7000h
MVI E,0Ah
MOV D,E ; 7002
LXI H,9000h
MOV A,M
MOV C,A ; 7007
INX H ; 7008
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
DCR E       ; 7019
JNZ 7002
HLT








