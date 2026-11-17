% ==============================================================================
% Directivas para predicados dinámicos
% ==============================================================================
:- dynamic equipo/1.
:- dynamic jugador/1.
:- dynamic jugador/2.
:- dynamic mvp/1.
:- dynamic dpoy/1.
:- dynamic roty/1.
:- dynamic fmvp/1.

% ==============================================================================
% Base de conocimiento NBA: Equipos
% ==============================================================================
equipo(warriors).
equipo(spurs).
equipo(lakers).
equipo(timberwolves).
equipo(nuggets).
equipo(okc_thunder).
equipo(rockets).
equipo(jazz).
equipo(mavericks).

equipo(celtics).
equipo('76ers').       % Comillas necesarias porque empieza con un dígito
equipo(knicks).
equipo(pistons).
equipo(heat).
equipo(cavaliers).

% ==============================================================================
% Jugadores activos (aridad 2: jugador/equipo)
% ==============================================================================
jugador(stephen_curry, warriors).
jugador(lebron_james, '76ers').
jugador(giannis_antetokoumpo, heat).
jugador(nikola_jokic, nuggets).
jugador(shai_gilgeus_alexander, okc_thunder).
jugador(victor_wembanyama, spurs).
jugador(jaren_jackson_jr, jazz).
jugador(evan_mobley, cavaliers).
jugador(marcus_smart, rockets).
jugador(luka_doncic, lakers).
jugador(kevin_durant, rockets).
jugador(stephon_castle, spurs).
jugador(cooper_flagg, mavericks).
jugador(jaylen_brown, '76ers').

% ==============================================================================
% Jugadores históricos / sin equipo registrado (aridad 1)
% ==============================================================================
jugador(michael_jordan).
jugador(kobe_bryant).
jugador(shaquille_oneal).
jugador(wilt_chamberlain).
jugador(larry_bird).
jugador(kareem_abdul_jabbar).

% ==============================================================================
% Galardones individuales
% ==============================================================================
mvp(stephen_curry).
mvp(lebron_james).
mvp(giannis_antetokoumpo).
mvp(nikola_jokic).
mvp(shai_gilgeus_alexander).
mvp(kevin_durant).
mvp(michael_jordan).
mvp(kobe_bryant).
mvp(shaquille_oneal).
mvp(wilt_chamberlain).
mvp(larry_bird).
mvp(kareem_abdul_jabbar).

dpoy(victor_wembanyama).
dpoy(jaren_jackson_jr).
dpoy(evan_mobley).
dpoy(marcus_smart).
dpoy(giannis_antetokoumpo).

roty(lebron_james).
roty(luka_doncic).
roty(victor_wembanyama).
roty(stephon_castle).
roty(cooper_flagg).

fmvp(stephen_curry).
fmvp(lebron_james).
fmvp(nikola_jokic).
fmvp(giannis_antetokoumpo).
fmvp(shai_gilgeus_alexander).
fmvp(jaylen_brown).

% ==============================================================================
% Reglas de consulta
% ==============================================================================

tiene_equipo(Jugador) :-
    jugador(Jugador, _).

retirado(Jugador) :-
    jugador(Jugador),
    \+ tiene_equipo(Jugador).

mvp_fmvp(Jugador) :-
    mvp(Jugador),
    fmvp(Jugador).

mvp_dpoy(Jugador) :-
    mvp(Jugador),
    dpoy(Jugador).

mvp_roy(Jugador) :-
    mvp(Jugador),
    roty(Jugador).

% ==============================================================================
% Reglas dinámicas: Gestión de Galardones
% ==============================================================================

% Agrega un galardón individual validando que el jugador exista en la base
agregar_galardon(Jugador, mvp) :-
    (   mvp(Jugador) -> true ; assertz(mvp(Jugador))   ).
agregar_galardon(Jugador, dpoy) :-
    (   dpoy(Jugador) -> true ; assertz(dpoy(Jugador))  ).
agregar_galardon(Jugador, roty) :-
    (   roty(Jugador) -> true ; assertz(roty(Jugador))  ).
agregar_galardon(Jugador, fmvp) :-
    (   fmvp(Jugador) -> true ; assertz(fmvp(Jugador))  ).

% Procesa una lista de premios [mvp, roty, ...] de forma recursiva
asignar_galardones(_, []).
asignar_galardones(Jugador, [Premio | Resto]) :-
    agregar_galardon(Jugador, Premio),
    asignar_galardones(Jugador, Resto).

% ==============================================================================
% Reglas dinámicas: Inserción de Equipos y Jugadores
% ==============================================================================

% 1. Agregar nuevo equipo
agregar_equipo(Equipo) :-
    (   equipo(Equipo)
    ->  write('Error: el equipo ya existe en el registro.'), nl, fail
    ;   assertz(equipo(Equipo)),
        format('Equipo ~w agregado exitosamente.~n', [Equipo])
    ).

% 2. Agregar jugador sin equipo (con lista de galardones opcional)
agregar_jugador(Jugador) :-
    agregar_jugador(Jugador, []).

agregar_jugador(Jugador, Galardones) :-
    (   (jugador(Jugador) ; jugador(Jugador, _))
    ->  write('Error: el jugador ya está registrado en el sistema.'), nl, fail
    ;   assertz(jugador(Jugador)),
        asignar_galardones(Jugador, Galardones),
        format('Jugador ~w agregado (sin equipo) con sus galardones.~n', [Jugador])
    ).

% 3. Agregar jugador con equipo (y lista de galardones opcional)
agregar_jugador(Jugador, Equipo, Galardones) :-
    (   \+ equipo(Equipo)
    ->  write('Error: el equipo indicado no existe en la base de datos.'), nl, fail
    ;   jugador(Jugador, Equipo)
    ->  write('Error: el jugador ya milita en ese equipo.'), nl, fail
    ;   assertz(jugador(Jugador, Equipo)),
        asignar_galardones(Jugador, Galardones),
        format('Jugador ~w asignado a ~w con galardones procesados.~n', [Jugador, Equipo])
    ).