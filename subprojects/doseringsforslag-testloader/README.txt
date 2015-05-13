
Lav først en jar fil med

gradle clean fatJar

Kør derefter main som sådan

java -jar build/libs/doseringsforslag-testloader-standalone.jar <sti-til-input.txt>

ps. test1 bliver en 'smule' belastet
pps. I Idea skal de 2 source mapper ../../src-noproducion og ../../src tilføjes til projektet manuelt efter hver opdatering :(
