grammar CodexLatinus;

@header {
package codexlatinus;
}

// PARSER
    programa: seccionDeclaraciones? seccionFunciones? seccionCodigo FINIS_MAYUS PUNTOYCOMA EOF;

    // Secciones
        seccionDeclaraciones: VARIABILES_GLOBAL (declaracion | definicionEstructura)*;
        seccionFunciones: MUNERA definicionFuncion*;
        seccionCodigo: MAIOR instruccion*;

    // Estructuras
        definicionEstructura: STRUCTURA ID LLAVE_IZQ (atributoEstructura)+ LLAVE_DER FINIS PUNTOYCOMA;

        atributoEstructura: (ESTO ID DOSPUNTOS tipo (PUNTOYCOMA | COMA))
                        | (SERIES ID DOSPUNTOS tipo (PUNTOYCOMA | COMA));

    // Funciones
        definicionFuncion: ACTIO ID PAREN_IZQ parametros? PAREN_DER LLAVE_IZQ seccionVariables? instruccion* LLAVE_DER FINIS PUNTOYCOMA
                        | RATIO tipo ID PAREN_IZQ parametros? PAREN_DER LLAVE_IZQ seccionVariables? instruccion* LLAVE_DER FINIS PUNTOYCOMA;

        seccionVariables: VARIABILES_LOCAL CORCH_IZQ (declaracion | definicionEstructura)* CORCH_DER;
        parametros: parametro (COMA parametro)*;
        parametro: ESTO ID DOSPUNTOS tipo;

    // Declaraciones
        declaracion: ESTO ID DOSPUNTOS tipo (valorInicial)? PUNTOYCOMA #DeclaracionVariable
                | ESTO ID DOSPUNTOS (VERUM | FALSUS) PUNTOYCOMA  #DeclaracionBoolSinTipo
                | ESTO ID DOSPUNTOS tipo estructuraInicial #DeclaracionEstructura
                | SERIES ID CORCH_IZQ expresion CORCH_DER DOSPUNTOS tipo (arr_valores)? PUNTOYCOMA  #DeclaracionArray
                | SERIES ID CORCH_IZQ expresion CORCH_DER DOSPUNTOS arr_valores PUNTOYCOMA #DeclaracionArrayBooleano;

        estructuraInicial: LLAVE_IZQ atributos_valores LLAVE_DER;
        arr_valores: LLAVE_IZQ valorArray (COMA valorArray)* LLAVE_DER;
        valorInicial: expresion | estructuraInicial;
        valorArray: expresion | estructuraAnonima;
        atributos_valores: atributo_valor (COMA atributo_valor)*;
        atributo_valor: ID DOSPUNTOS valorAtributo;
        valorAtributo: expresion
                | ID CORCH_IZQ NUMERO CORCH_DER
                | estructuraAnonima
                | arr_valores;

    // Instrucciones
        instruccion: asignacion PUNTOYCOMA
                | incremento PUNTOYCOMA 
                | expresion PUNTOYCOMA
                | condicional
                | bucle
                | interrupcion
                | retorno
                | impresion
                | lectura;

        asignacion: ID IGUAL expresion  #AsigVariable
                | ID CORCH_IZQ expresion CORCH_DER IGUAL expresion #AsigArreglo
                | ID PUNTO ID IGUAL expresion #AsigAtributo
                | ID PUNTO ID CORCH_IZQ expresion CORCH_DER IGUAL expresion #AsigAtributoArray
                | ID CORCH_IZQ expresion CORCH_DER IGUAL estructuraAnonima #AsigArrayEstructura;

        estructuraAnonima: LLAVE_IZQ atributos_valores LLAVE_DER;
        retorno: REDDERE expresion? PUNTOYCOMA;
        impresion: IMPRIMIR expresion (IMPRIMIR expresion)* PUNTOYCOMA;
        lectura: LECTURA
                | ID LECTURA;
        incremento: ID (MAS_ABREVIADO | MENOS_ABREVIADO) #IncrementoVariable
                | ID CORCH_IZQ expresion CORCH_DER (MAS_ABREVIADO | MENOS_ABREVIADO) #IncrementoArray
                | ID PUNTO ID (MAS_ABREVIADO | MENOS_ABREVIADO) #IncrementoAtributo;

    // Expresiones
        expresion: expresion (MAS | MENOS) termino #SumaResta
            | expresion (MAYOR | MAYOR_IGUAL | MENOR | MENOR_IGUAL) termino #Comparacion
            | expresion (IGUALIGUAL | DIFERENTEDE) termino #Igualdad
            | expresion AND termino #AndLogico
            | expresion OR termino #OrLogico
            | termino #toTermino;

        termino: termino (POR | DIVISION) factor
            | factor;

        factor: NUMERO #NumLiteral
            | DECIMALES #DecLiteral
            | TEXTO #TextLiteral
            | CARACTER #CharLiteral
            | VERUM #TrueLiteral
            | FALSUS #FalseLiteral
            | NON factor #Negacion
            | ID #Variable
            | ID CORCH_IZQ expresion CORCH_DER #AccesoArray
            | ID PAREN_IZQ argumentos? PAREN_DER #LlamadaFuncion
            | PAREN_IZQ expresion PAREN_DER #Parentesis
            | factor PUNTO ID #AccesoAtributo
            | factor PUNTO ID CORCH_IZQ expresion CORCH_DER #AccesoAtributoArray;

        argumentos: expresion (COMA expresion)*;

    // Tipos
        tipo: tipoSimple | SERIES tipoSimple;
        tipoSimple: NUMERUS | TEXTUM | DECIMALIS | LITTERA | BOOL | ID;

    // Condicionales
        condicional: SI PAREN_IZQ expresion PAREN_DER bloque (ALITER PAREN_IZQ expresion PAREN_DER bloque)* (ALITER bloque)? FINIS PUNTOYCOMA;

        bloque: LLAVE_IZQ instruccion* LLAVE_DER;

    // Bucles
        bucle: bucleDum | bucleFacere | buclePer;
        bucleDum: DUM PAREN_IZQ expresion PAREN_DER bloque FINIS PUNTOYCOMA;
        bucleFacere: FACERE bloque DUM PAREN_IZQ expresion PAREN_DER PUNTOYCOMA;
        buclePer: PER PAREN_IZQ declaracion expresion PUNTOYCOMA actualizacion? PAREN_DER bloque;

        actualizacion: ID (MAS_ABREVIADO | MENOS_ABREVIADO)
                | asignacion;

        interrupcion: PERGE PUNTOYCOMA
                | INTERRUMPE PUNTOYCOMA;

// LEXER
    // EXPRESIONES
        // Palabras Reservadas de Secciones
            VARIABILES_GLOBAL: 'VARIABILES>';
            VARIABILES_LOCAL: 'VARIABILES';
            MUNERA: 'MUNERA>';
            MAIOR: 'MAIOR>';

        // Palabras Reservadas
            ESTO: 'esto'; // Declaracion de variable
            SERIES: 'series'; // Arreglos
            SI: 'si';
            ALITER: 'aliter';
            DUM: 'dum';
            FACERE: 'facere';
            PER: 'per';
            PERGE: 'perge';
            INTERRUMPE: 'interrumpe';
            STRUCTURA: 'structura';
            FINIS: 'finis';
            FINIS_MAYUS: 'FINIS';
            ACTIO: 'actio';
            RATIO: 'ratio';
            REDDERE: 'reddere';

        // Booleano
            VERUM: 'verum';
            FALSUS: 'falsus';

        // Tipos de datos
            NUMERUS: 'numerus';
            TEXTUM: 'textum';
            DECIMALIS: 'decimalis';
            LITTERA: 'littera';
            BOOL: 'bool';

        // Funciones
            IMPRIMIR: '>>';
            LECTURA: '<<';

        // Basicas
            ID: [a-zA-Z][a-zA-Z0-9_]* ;
            NUMERO: [0-9]+;
            DECIMALES: [0-9]+'.'[0-9]+;
            TEXTO: '"' .*? '"';
            CARACTER: '\'' . '\'';
            WS: [ \t\r]+ -> skip;
            FIN_LINEA: '\n' -> skip;        


    // COMENTARIOS
        COMENTARIO_LINEA: '//' ~[\r\n]* -> skip;
        COMENTARIO_BLOQUE: '##' .*? '##' -> skip;

    // OPERADORES
            // Aritmeticos
            MAS: '+';
            MENOS: '-';
            POR: '*';
            DIVISION: '/';
            MAS_ABREVIADO: '++';
            MENOS_ABREVIADO: '--';

            // Relacionales
            IGUALIGUAL: '==';
            DIFERENTEDE: '!=';
            MENOR: '<';
            MENOR_IGUAL: '<=';
            MAYOR: '>';
            MAYOR_IGUAL: '>=';

            // Logicos
            AND: '&&';
            OR: '||';

            // Negacion
            NON: 'non';

            // Declaratorio
            DOSPUNTOS: ':';
            IGUAL: '=';

            //Otro
            COMA: ',';
            PUNTOYCOMA: ';';
            PUNTO: '.';

            // Agrupacion
            LLAVE_IZQ: '{';
            LLAVE_DER: '}';
            CORCH_IZQ: '[';
            CORCH_DER: ']';
            PAREN_IZQ: '(';
            PAREN_DER: ')';