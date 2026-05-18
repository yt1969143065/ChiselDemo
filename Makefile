check1:
	./mill Demo.runMain pRed.Main
run:
	./mill Demo.runMain gcd.GCD
clean:
	rm -rf out *.sv *.f
