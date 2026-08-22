ARIABILESVay>
estoway edadway : umerusnay 20;
estoway ifradocay : alsusfay;
estoway omandantecay : extumtay "Estudiante X";
estoway uerzafay : umerusnay 10;
estoway oderpay : umerusnay 0;
UNERAMay>
atioray umerusnay alcularPodercay(estoway uerzafay : umerusnay) {
ARIABILESVay[
estoway otaltay : umerusnay uerzafay * 2;
]
eddereray otaltay;
} inisfay;

AIORMay>
%OINK "Hola comandante!";
%OINK "Ingresa tu nombre por favor";
omandantecay %OINK_OINK
%OINK "Bienvenido" %OINK omandantecay;
%OINK "Ingresa tu edad";
isay (edadway >= 18) {
ifradocay = erumvay;
uerzafay = 12;

} inisfay;

%OINK "Tu poder es:" %OINK alcularPodercay(uerzafay);
%OINK "La puerta esta cifrada?" %OINK ifradocay;
INISFay;
