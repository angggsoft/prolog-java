% ========================================================
%  Base de Conocimiento: Dragones de House of the Dragon
% ========================================================

:- dynamic dragon/1.
:- dynamic jinete/2.
:- dynamic bando/2.
:- dynamic color/2.
:- dynamic salvaje/1.
:- dynamic nacido_en/2.

% --- Hechos: Declaración de Dragones ---
dragon(balerion).
dragon(meraxes).
dragon(vhagar).
dragon(caraxes).
dragon(syrax).
dragon(meleys).
dragon(sunfyre).
dragon(seasmoke).
dragon(vermithor).
dragon(silverwing).
dragon(arrax).
dragon(vermax).
dragon(moondancer).
dragon(dreamfyre).
dragon(tessarion).
dragon(canibal).
dragon(robaovejas).

% --- Hechos: Jinetes (Varios dragones han tenido múltiples jinetes) ---

% Balerion el Terror Negro
jinete(balerion, aegon_i_targaryen).
jinete(balerion, maegor_i_targaryen).
jinete(balerion, aerea_targaryen).
jinete(balerion, viserys_i_targaryen).

% Vhagar la Reina de la Muerte
jinete(vhagar, visenya_targaryen).
jinete(vhagar, baelon_targaryen).
jinete(vhagar, laena_velaryon).
jinete(vhagar, aemond_targaryen).

% Meraxes
jinete(meraxes, rhaenys_targaryen_conquistadora).

% Caraxes el Anfíptero de Sangre
jinete(caraxes, aemon_targaryen).
jinete(caraxes, daemon_targaryen).

% Meleys la Reina Roja
jinete(meleys, alyssa_targaryen).
jinete(meleys, rhaenys_targaryen).

% Vermithor la Furia de Bronce
jinete(vermithor, jaehaerys_i_targaryen).
jinete(vermithor, hugh_hammer).

% Silverwing Ala de Plata
jinete(silverwing, alysanne_targaryen).
jinete(silverwing, ulf_white).

% Seasmoke Bruma Marina
jinete(seasmoke, laenor_velaryon).
jinete(seasmoke, addam_of_hull).

% Dreamfyre Fuegoensueño
jinete(dreamfyre, rhaena_targaryen).
jinete(dreamfyre, helaena_targaryen).

% Jinetes únicos en la Danza
jinete(syrax, rhaenyra_targaryen).
jinete(sunfyre, aegon_ii_targaryen).
jinete(arrax, lucerys_velaryon).
jinete(vermax, jacaerys_velaryon).
jinete(moondancer, baela_targaryen).
jinete(tessarion, daeron_targaryen).
jinete(robaovejas, ortigas).

% Caníbal no tiene jinete (completamente salvaje e indomable)

% --- Hechos: Bando en la Danza de Dragones ---
bando(vhagar, verdes).
bando(sunfyre, verdes).
bando(dreamfyre, verdes).
bando(tessarion, verdes).

bando(caraxes, negros).
bando(syrax, negros).
bando(meleys, negros).
bando(seasmoke, negros).
bando(vermithor, negros).
bando(silverwing, negros).
bando(arrax, negros).
bando(vermax, negros).
bando(moondancer, negros).
bando(robaovejas, negros).

bando(balerion, aegon_el_conquistador).
bando(meraxes, aegon_el_conquistador).

% --- Hechos: Colores y Características distintivas ---
color(balerion, negro_ceniza).
color(meraxes, gris_plateado).
color(vhagar, bronce_y_verde).
color(caraxes, rojo_sangre).
color(syrax, amarillo_dorado).
color(meleys, rojo_escarlata).
color(sunfyre, oro_brillante).
color(seasmoke, gris_plateado).
color(vermithor, bronce).
color(silverwing, plateado).
color(arrax, blanco_perlado).
color(vermax, verde_oliva).
color(moondancer, verde_claro).
color(dreamfyre, azul_palido).
color(tessarion, azul_cobalto).
color(canibal, negro_carbon).
color(robaovejas, marron_pardo).

% --- Hechos: Dragones salvajes ---
salvaje(canibal).
salvaje(robaovejas).

% --- Hechos: Lugar de Nacimiento / Origen ---
nacido_en(balerion, valyria).
nacido_en(vhagar, dragonstone).
nacido_en(meraxes, dragonstone).
nacido_en(canibal, desconocido).
nacido_en(robaovejas, dragonstone).
nacido_en(silverwing, dragonstone).
nacido_en(vermithor, dragonstone).
nacido_en(dreamfyre, dragonstone).
nacido_en(caraxes, dragonstone).
nacido_en(meleys, dragonstone).
nacido_en(syrax, dragons_nest).
nacido_en(sunfyre, dragonstone).
nacido_en(seasmoke, dragonstone).
nacido_en(arrax, dragonstone).
nacido_en(vermax, dragonstone).
nacido_en(moondancer, dragonstone).
nacido_en(tessarion, dragonstone).

% ========================================================
%  Reglas (Lógica e Inferencia)
% ========================================================

% Un dragón tiene jinete si existe al menos un registro
tiene_jinete(Dragon) :-
    jinete(Dragon, _).

% Un dragón no tiene jinete si es salvaje o no tiene jinete registrado
sin_jinete(Dragon) :-
    dragon(Dragon),
    \+ tiene_jinete(Dragon).

% Dragones que han tenido múltiples jinetes a lo largo de su historia
multiples_jinetes(Dragon) :-
    dragon(Dragon),
    findall(J, jinete(Dragon, J), Jinetes),
    length(Jinetes, N),
    N > 1.

% Obtener todos los jinetes de un dragón como lista
jinetes_de(Dragon, Jinetes) :-
    dragon(Dragon),
    findall(J, jinete(Dragon, J), Jinetes).

% Obtener todos los dragones que ha montado un jinete
dragones_de_jinete(Jinete, Dragones) :-
    findall(D, jinete(D, Jinete), Dragones).

% Dos dragones compartieron jinete si el mismo jinete los montó a ambos
comparten_jinete(Dragon1, Dragon2, Jinete) :-
    jinete(Dragon1, Jinete),
    jinete(Dragon2, Jinete),
    Dragon1 \= Dragon2.

% Dos dragones son aliados si pertenecen al mismo bando y no son el mismo dragón
son_aliados(Dragon1, Dragon2) :-
    bando(Dragon1, Bando),
    bando(Dragon2, Bando),
    Dragon1 \= Dragon2.

% Dos dragones son enemigos / rivales si pertenecen a bandos contrarios
son_enemigos(Dragon1, Dragon2) :-
    bando(Dragon1, Bando1),
    bando(Dragon2, Bando2),
    Bando1 \= Bando2.

% Obtener el bando a través del jinete
bando_del_jinete(Jinete, Bando) :-
    jinete(Dragon, Jinete),
    bando(Dragon, Bando).

% Regla para registrar dinámicamente un nuevo jinete
agregar_jinete(Dragon, Jinete) :-
    (   dragon(Dragon) -> true ; assertz(dragon(Dragon)) ),
    (   jinete(Dragon, Jinete) -> true ; assertz(jinete(Dragon, Jinete)) ).
