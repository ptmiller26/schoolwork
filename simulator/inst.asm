		LI 		R1, 100h 	# addr = 0x100;
		LW 		R3, 0(R1) 	# boundary = *addr;
		LI 		R5, 1 		# i = 1;
		LI 		R7, 0h 		# sum = 0;
		LI 		R6, 1h 		# factorial = 0x01;
LOOP:	MULT 	R6, R5, R6 	# factorial *= I;
		ADD 	R7, R7, R6 	# sum += factorial;
		ADDI 	R5, R5, 1h 	# i++;
		BNE 	R5, R3, LOOP
		HLT
		HLT