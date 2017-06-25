# makefile for asteroids
# command to build project:
# -> make
# command to run project:
# -> make run
# or do both with
# -> make exec

objects = src/Asteroid.java src/Bullet.java src/Entity.java src/GameFrame.java src/GameLogic.java \
		src/GameSounds.java src/Main.java src/Particle.java src/Player.java

all: $(objects)
	javac $(objects) -d out/

exec: all
	java -Dsun.java2d.opengl=true -cp out/ Main

run:
	java -Dsun.java2d.opengl=true -cp out/ Main
