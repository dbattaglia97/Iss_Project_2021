:- dynamic(slotFree/1).
:-dynamic(indoorfree/0).
:-dynamic(outdoorfree/0).
:-dynamic(token/1).




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


acceptIN :- indoorfree,availableParking,!.
acceptIN :- indoorfree,availableParking.

acceptOUT:-outdoorfree,!.
acceptOUT:-outdoorfree.



occupiedindoor :- retract(indoorfree).

occupiedoutdoor :- retract(outdoorfree).

freedindoor:- assert(indoorfree).

freedoutdoor:- assert(outdoorfree).

occupySlot(N):- retract(slotFree(N)),!.

vacateSlot(N):- assert(slotFree(N)),!.

addToken(T) :- assert(token(T)),!.

removeToken(T) :- retract(token(T)).
