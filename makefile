# makefile for asteroids
# command to build project:
# -> make
# command to run project:
# -> make exec

objects = src/Asteroid.java src/Bullet.java src/Entity.java src/GameFrame.java src/GamePanel.java \
		src/Main.java src/Particle.java src/Player.java

all: $(objects)
	javac $(objects) -d out/

exec: all
	java -cp out/ Main
