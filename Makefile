compile:
	mkdir -p bin
	javac src/*.java -d bin
	cp -r src/images bin/images
