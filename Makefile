run:
	./mill Demo.runMain gcd.GCD
check1:
	./mill Demo.runMain pRed.Main
clean:
	rm -rf out *.sv *.f
