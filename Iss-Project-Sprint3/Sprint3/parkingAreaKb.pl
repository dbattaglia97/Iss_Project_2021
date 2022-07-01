:- dynamic(slotFree/1).
:-dynamic(indoorfree/0).
:-dynamic(outdoorfree/0).
:-dynamic(token/1).
:-dynamic(trolleyIdle/0).
:-dynamic(trolleyWorking/0).
:-dynamic(trolleyStopped/0).



slotFree(1).
slotFree(2).
slotFree(3).
slotFree(4).
slotFree(5).
slotFree(6).

availableParking :- slotFree(1),!.
availableParking :- slotFree(2),!.
availableParking :- slotFree(3),!.
availableParking :- slotFree(4),!.
availableParking :- slotFree(5),!.
availableParking :- slotFree(6),!.


indoorfree.
outdoorfree.


acceptIN :- indoorfree,availableParking,trolleyIdle,!.
acceptIN :- indoorfree,availableParking,trolleyWorking.

acceptOUT:-outdoorfree,trolleyIdle,!.
acceptOUT:-outdoorfree,trolleyWorking.


occupiedindoor :- retract(indoorfree).

occupiedoutdoor :- retract(outdoorfree).

freedindoor:- assert(indoorfree).

freedoutdoor:- assert(outdoorfree).

occupySlot(N):- retract(slotFree(N)),!.

vacateSlot(N):- assert(slotFree(N)),!.

addToken(T) :- assert(token(T)),!.

removeToken(T) :- retract(token(T)).

trolleyIdle.

changeToWorking :- retract(trolleyIdle),assert(trolleyWorking),!.
changeToWorking :- retract(trolleyStopped),assert(trolleyWorking).


changeToIdle :- retract(trolleyWorking),assert(trolleyIdle),!.
changeToIdle :- retract(trolleyStopped),assert(trolleyIdle).


changeToStopped :- retract(trolleyWorking),assert(trolleyStopped),!.
changeToStopped :- retract(trolleyIdle),assert(trolleyStopped).