        lw      0       2       start       ; Load Start into reg2
        lw      0       3       final       ; Load final into reg3
        lw      0       5       neg1        ; Load -1 into reg4
        add     2       5       2

check   beq     3       2       done        ; If start is final-1 , exit loop
        add     6       3       6           ; reg6 = reg6 + final (result)
        add     3       5       3           ; decrease final by 1
        beq     0       0       check       ; Continue the loop

done    halt                                ; End of program

final   .fill   1000                        ; Set final index
start   .fill   0                           ; Set start index
neg1    .fill   -1                          ; Set -1