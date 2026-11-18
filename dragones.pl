% ==============================================================================
%  BASE DE CONOCIMIENTOS: DRAGONES DE HOUSE OF THE DRAGON (HotD)
% ==============================================================================
%
%  GUÍA DIDÁCTICA DE PROLOG:
%  -------------------------
%  En Prolog (Programación Lógica), no escribimos "pasos a seguir" como en Java,
%  sino que declaramos HECHOS (verdades conocidas) y REGLAS (relaciones lógicas).
%
%  Conceptos clave para leer este archivo:
%  1. ÁTOMO: Es un valor constante o nombre propio. Siempre empieza en minúscula
%     y usa guion bajo en lugar de espacios (ej. caraxes, aemond_targaryen).
%  2. VARIABLE: Representa una incógnita que Prolog intentará resolver. Siempre
%     empieza con MAYÚSCULA o con guion bajo '_' (ej. Dragon, X, Jinete).
%     La variable '_' (anónima) significa "cualquier valor que no necesito guardar".
%  3. HECHO: Una afirmación verdadera que termina con un punto '.'
%     Ejemplo: dragon(vhagar).
%  4. REGLA: Una conclusión que es verdadera SI se cumplen sus condiciones.
%     Usa el símbolo ':-' que se lee como "SI..." o "SE CUMPLE CUANDO...".
%  5. ARIDAD (nombre/N): Indica cuántos argumentos recibe un predicado.
%     Por ejemplo, dragon/1 recibe 1 argumento; jinete/2 recibe 2 argumentos.
% ==============================================================================


% ==============================================================================
%  SECCIÓN 1: DECLARACIÓN DE PREDICADOS DINÁMICOS (:- dynamic)
% ==============================================================================
%  Por defecto, en Prolog los hechos son estáticos (solo lectura en memoria).
%  La directiva ':- dynamic nombre/aridad.' le indica al motor de Prolog que
%  estos predicados pueden modificarse en tiempo de ejecución desde Java usando:
%    - assertz(Hecho): Agrega un nuevo hecho al FINAL de la base en memoria.
%    - asserta(Hecho): Agrega un nuevo hecho al INICIO de la base en memoria.
%    - retract(Hecho): Elimina un hecho existente de la memoria.
% ------------------------------------------------------------------------------
:- dynamic dragon/1.
:- dynamic jinete/2.
:- dynamic bando/2.
:- dynamic color/2.
:- dynamic salvaje/1.
:- dynamic nacido_en/2.


% ==============================================================================
%  SECCIÓN 2: HECHOS - INVENTARIO DE DRAGONES (dragon/1)
% ==============================================================================
%  Predicado unario (aridad 1): dragon(NombreDragon).
%  Declara que el átomo especificado es un dragón válido dentro del sistema.
% ------------------------------------------------------------------------------
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


% ==============================================================================
%  SECCIÓN 3: HECHOS - RELACIÓN DRAGÓN Y JINETE (jinete/2)
% ==============================================================================
%  Predicado binario (aridad 2): jinete(NombreDragon, NombreJinete).
%  Representa una relación 1 a muchos (1:N): los dragones viven más que los
%  humanos, por lo que un mismo dragón puede tener MÚLTIPLES hechos jinete/2
%  a lo largo de distintas épocas. Cuando consultamos jinete(vhagar, J),
%  Prolog hará "backtracking" (retroceso) para devolver todos los jinetes.
% ------------------------------------------------------------------------------

jinete(balerion, aegon_i_targaryen).
jinete(balerion, maegor_i_targaryen).
jinete(balerion, aerea_targaryen).
jinete(balerion, viserys_i_targaryen).

jinete(vhagar, visenya_targaryen).
jinete(vhagar, baelon_targaryen).
jinete(vhagar, laena_velaryon).
jinete(vhagar, aemond_targaryen).

jinete(meraxes, rhaenys_targaryen_conquistadora).

jinete(caraxes, aemon_targaryen).
jinete(caraxes, daemon_targaryen).

jinete(meleys, alyssa_targaryen).
jinete(meleys, rhaenys_targaryen).

jinete(vermithor, jaehaerys_i_targaryen).
jinete(vermithor, hugh_hammer).

jinete(silverwing, alysanne_targaryen).
jinete(silverwing, ulf_white).

jinete(seasmoke, laenor_velaryon).
jinete(seasmoke, addam_of_hull).

jinete(dreamfyre, rhaena_targaryen).
jinete(dreamfyre, helaena_targaryen).

jinete(syrax, rhaenyra_targaryen).
jinete(sunfyre, aegon_ii_targaryen).
jinete(arrax, lucerys_velaryon).
jinete(vermax, jacaerys_velaryon).
jinete(moondancer, baela_targaryen).
jinete(tessarion, daeron_targaryen).
jinete(robaovejas, ortigas).

% Nota didáctica: 'canibal' NO tiene ningún hecho jinete/2 porque es un dragón
% completamente salvaje. En Prolog, lo que no está declarado o inferible se
% asume falso ("Suposición de Mundo Cerrado" o Closed World Assumption).


% ==============================================================================
%  SECCIÓN 4: HECHOS - BANDO EN LA DANZA DE DRAGONES (bando/2)
% ==============================================================================
%  Predicado binario: bando(NombreDragon, NombreBando).
%  Asocia cada dragón con la facción política o época en la que luchó.
% ------------------------------------------------------------------------------

% Facción de Los Verdes (Rey Aegon II)
bando(vhagar, verdes).
bando(sunfyre, verdes).
bando(dreamfyre, verdes).
bando(tessarion, verdes).

% Facción de Los Negros (Reina Rhaenyra)
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

% Época de la Conquista de Aegon (casi un siglo antes de la Danza)
bando(balerion, aegon_el_conquistador).
bando(meraxes, aegon_el_conquistador).


% ==============================================================================
%  SECCIÓN 5: HECHOS - COLOR CARACTERÍSTICO (color/2)
% ==============================================================================
%  Predicado binario: color(NombreDragon, Color).
%  Define el rasgo visual principal de las escamas de cada dragón.
% ------------------------------------------------------------------------------
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


% ==============================================================================
%  SECCIÓN 6: HECHOS - DRAGONES SALVAJES (salvaje/1)
% ==============================================================================
%  Predicado unario: salvaje(NombreDragon).
%  Indica qué dragones crecieron en libertad sin vínculo inicial en Pozo Dragón.
% ------------------------------------------------------------------------------
salvaje(canibal).
salvaje(robaovejas).


% ==============================================================================
%  SECCIÓN 7: HECHOS - LUGAR DE NACIMIENTO U ORIGEN (nacido_en/2)
% ==============================================================================
%  Predicado binario: nacido_en(NombreDragon, Lugar).
%  Registra dónde eclosionó el huevo de cada dragón.
% ------------------------------------------------------------------------------
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


% ==============================================================================
%  SECCIÓN 8: REGLAS DE INFERENCIA LÓGICA
% ==============================================================================
%  Aquí el sistema experto "razona" combinando hechos para deducir respuestas
%  nuevas que no están escritas explícitamente como datos simples.
%
%  Operadores lógicos utilizados:
%    ','  -> Y lógico (conjunción): ambas condiciones deben cumplirse.
%    ';'  -> O lógico (disyunción): basta con que se cumpla una condición.
%    '\+' -> Negación por fallo (NOT): verdadero si Prolog NO logra probar la meta.
%    '\=' -> Desigualdad: verdadero si los dos términos son distintos.
% ------------------------------------------------------------------------------

% --- Regla 1: tiene_jinete/1 ---
% Un dragón tiene jinete SI existe al menos un hecho jinete(Dragon, _).
% Usamos '_' porque en esta regla no nos importa el nombre del jinete, solo si existe.
tiene_jinete(Dragon) :-
    jinete(Dragon, _).

% --- Regla 2: sin_jinete/1 ---
% Un dragón no tiene jinete SI es un dragón válido Y NO se cumple tiene_jinete(Dragon).
% Nota: Es fundamental poner primero 'dragon(Dragon)' para instanciar la variable
% Dragon antes de evaluar la negación '\+'.
sin_jinete(Dragon) :-
    dragon(Dragon),
    \+ tiene_jinete(Dragon).

% --- Regla 3: multiples_jinetes/1 ---
% Un dragón ha tenido múltiples jinetes SI:
%   1) Es un dragón válido: dragon(Dragon)
%   2) Recolectamos en la lista 'Jinetes' todos los J tales que jinete(Dragon, J)
%      usando el predicado predefinido findall(Plantilla, Meta, ListaResultado).
%   3) Calculamos la longitud 'N' de esa lista con length(Lista, Longitud).
%   4) Verificamos aritméticamente que N > 1.
multiples_jinetes(Dragon) :-
    dragon(Dragon),
    findall(J, jinete(Dragon, J), Jinetes),
    length(Jinetes, N),
    N > 1.

% --- Regla 4: jinetes_de/2 ---
% Devuelve en el segundo argumento una lista con todos los jinetes de un dragón.
% Ejemplo de consulta: ?- jinetes_de(vhagar, Lista).
jinetes_de(Dragon, Jinetes) :-
    dragon(Dragon),
    findall(J, jinete(Dragon, J), Jinetes).

% --- Regla 5: dragones_de_jinete/2 ---
% Devuelve en una lista todos los dragones que ha montado un mismo jinete.
% Ejemplo de consulta: ?- dragones_de_jinete(aemond_targaryen, Lista).
dragones_de_jinete(Jinete, Dragones) :-
    findall(D, jinete(D, Jinete), Dragones).

% --- Regla 6: comparten_jinete/3 ---
% Dos dragones (Dragon1 y Dragon2) compartieron jinete SI:
%   1) El mismo Jinete montó a Dragon1, Y
%   2) Ese mismo Jinete montó a Dragon2, Y
%   3) Dragon1 y Dragon2 son dragones diferentes (Dragon1 \= Dragon2).
comparten_jinete(Dragon1, Dragon2, Jinete) :-
    jinete(Dragon1, Jinete),
    jinete(Dragon2, Jinete),
    Dragon1 \= Dragon2.

% --- Regla 7: son_aliados/2 ---
% Dos dragones son aliados SI pertenecen exactamente al mismo Bando
% Y no se está comparando el dragón consigo mismo (Dragon1 \= Dragon2).
son_aliados(Dragon1, Dragon2) :-
    bando(Dragon1, Bando),
    bando(Dragon2, Bando),
    Dragon1 \= Dragon2.

% --- Regla 8: son_enemigos/2 ---
% Dos dragones son enemigos / rivales SI:
%   1) Dragon1 pertenece a Bando1, Y
%   2) Dragon2 pertenece a Bando2, Y
%   3) Sus bandos son diferentes (Bando1 \= Bando2).
son_enemigos(Dragon1, Dragon2) :-
    bando(Dragon1, Bando1),
    bando(Dragon2, Bando2),
    Bando1 \= Bando2.

% --- Regla 9: bando_del_jinete/2 ---
% Regla transitiva: deduce el bando de un jinete buscando qué dragón monta
% y consultando el bando de ese dragón.
bando_del_jinete(Jinete, Bando) :-
    jinete(Dragon, Jinete),
    bando(Dragon, Bando).

% --- Regla 10: agregar_jinete/2 (Modificación dinámica en memoria) ---
% Permite registrar un nuevo jinete para un dragón evitando duplicados.
% Usa la estructura condicional de Prolog: ( Condición -> Si_Es_Verdad ; Si_No ).
%   1) Si dragon(Dragon) ya existe, no hace nada (true); si no, lo inserta con assertz.
%   2) Si jinete(Dragon, Jinete) ya existe, no lo duplica; si no, lo inserta con assertz.
agregar_jinete(Dragon, Jinete) :-
    (   dragon(Dragon) -> true ; assertz(dragon(Dragon)) ),
    (   jinete(Dragon, Jinete) -> true ; assertz(jinete(Dragon, Jinete)) ).

%Reglas de Carlos Mota
% --- Regla 11: dragones_del_bando/2 ---
% Devuelve en una lista todos los dragones que pertenecen a un bando.
% Usa findall(Plantilla, Meta, Lista) para recolectar cada D tal que bando(D, Bando).
% Importante: hay que consultarla con un bando concreto (verdes, negros, etc.).
% Ejemplo de consulta: ?- dragones_del_bando(negros, Lista).
dragones_del_bando(Bando, Dragones) :-
    findall(D, bando(D, Bando), Dragones).

% --- Regla 12: dragon_libre/1 ---
% Un dragón es libre SI es salvaje Y además no tiene ningún jinete registrado.
% Combina un hecho (salvaje/1) con una regla (sin_jinete/1), que a su vez usa
% negación por fallo (\+).
% Ejemplo de consulta: ?- dragon_libre(D).
dragon_libre(Dragon) :-
    salvaje(Dragon),
    sin_jinete(Dragon).

% --- Regla 13: dragon_domado/1 ---
% Un dragón fue domado SI nació salvaje Y ya tiene al menos un jinete.
% Es lo contrario de dragon_libre/1: aquí usamos tiene_jinete/1 en lugar de sin_jinete/1.
% Ejemplo de consulta: ?- dragon_domado(D).
dragon_domado(Dragon) :-
    salvaje(Dragon),
    tiene_jinete(Dragon).






  
% Ejemplo de Carlos Josue
% --- Regla adicional 1: dragones_por_color/2 ---
% Devuelve una lista con todos los dragones que tienen un color específico.
%
% 1. Recibe un color.
% 2. Busca los dragones que tienen ese color.
% 3. Utiliza findall para reunirlos en una lista.

dragones_por_color(Color, Dragones) :-
    findall(D, color(D, Color), Dragones).
% --- Regla adicional 2: dragones_por_origen/2 ---
% Devuelve una lista con todos los dragones que nacieron
% en un lugar específico.
%
% 1. Recibe un lugar de nacimiento.
% 2. Busca los dragones asociados a ese lugar.
% 3. Utiliza findall para reunir los resultados.

dragones_por_origen(Lugar, Dragones) :-
    findall(D, nacido_en(D, Lugar), Dragones).
% --- Regla adicional 3: dragones_con_jinete_por_bando/2 ---
% Devuelve una lista de dragones pertenecientes a un bando
% que tienen al menos un jinete registrado.
%
% 1. Busca los dragones pertenecientes al bando solicitado.
% 2. Comprueba que cada dragón tenga al menos un jinete.
% 3. Reúne todos los resultados mediante findall.

dragones_con_jinete_por_bando(Bando, Dragones) :-
    findall(
        D,
        (bando(D, Bando), tiene_jinete(D)),
        Dragones
    ).

% Reglas de Leonardo Mariaud
% --- Regla: dragon_de_la_danza/1 ---
% Un dragón peleó en la Danza de Dragones SI es de los verdes O de los negros.
% Usa ';' (O lógico): basta con que se cumpla una de las dos condiciones.
dragon_de_la_danza(Dragon) :-
    bando(Dragon, verdes) ;
    bando(Dragon, negros).

% --- Regla: enemigos_en_la_danza/2 ---
% Dos dragones son enemigos en la Danza SI son enemigos Y ambos pelearon en la Danza.
% Reutiliza dos reglas: son_enemigos/2 y dragon_de_la_danza/1.
enemigos_en_la_danza(Dragon1, Dragon2) :-
    son_enemigos(Dragon1, Dragon2),
    dragon_de_la_danza(Dragon1),
    dragon_de_la_danza(Dragon2).

% --- Regla: eliminar_jinete/2 ---
% Borra de la memoria el hecho jinete(Dragon, Jinete). Es lo contrario de agregar_jinete/2.
% Funciona porque jinete/2 está declarado como dynamic.
eliminar_jinete(Dragon, Jinete) :-
    retract(jinete(Dragon, Jinete)).