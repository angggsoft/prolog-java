% Base de conocimiento NBA

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
equipo(76ers).
equipo(knicks).
equipo(pistons).
equipo(heat).
equipo(cavaliers).

jugador(stephen_curry, warriors).
jugador(lebron_james, 76ers).
jugador(giannis_antetokoumpo, heat).
jugador(nikola_jokic,nuggets).
jugador(shai_gilgeus_alexander,okc_thunder).
jugador(victor_wembanyama,spurs).
jugador(jaren_jackson_jr,jazz).
jugador(evan_mobley,cavaliers).
jugador(marcus_smart,rockets).
jugador(luka_doncic,lakers).
jugador(kevin_durant,rockets).
jugador(stephon_castle,spurs).
jugador(cooper_flagg,mavericks).
jugador(jaylen_brown, 76ers).

jugador(michael_jordan).
jugador(kobe_bryant).
jugador(shaquille_oneal).
jugador(wilt_chamberlain).
jugador(larry_bird).
jugador(kareem_abdul_jabbar).

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

fmvp(curry).
fmvp(lebron_james).
fmvp(nikola_jokic).
fmvp(giannis_antetokoumpo).
fmvp(shai_gilgeus_alexander).
fmvp(jaylen_brown).

%Reglas , es un and

% Comprueba si un jugador tiene contrato/equipo activo
tiene_equipo(Jugador) :-
    jugador(Jugador, _).

retirado(Jugador) :-
    jugador(Jugador).
    \+ tiene_equipo(Jugador).

mvp_fmvp(Jugador) :-
    mvp(Jugador), fmvp(Jugador).

mvp_dpoy(Jugador) :-
    mvp(Jugador), dpoy(Jugador).

mvp_roy(Jugador) :-
    mvp(Jugador), roy(Jugador).

agregar_jugador(Jugador, Equipo) :-
    jugador(Jugador, Equipo),
    !,
    write('Error, este jugador ya ha sido registrado.'), nl, fail;
    assertz(jugador(Jugador, Equipo)),
    write('El jugador ha sido agregado.').