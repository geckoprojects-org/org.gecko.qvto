/**
* <copyright>
*
* Copyright (c) 2005, 2007 IBM Corporation and others.
* All rights reserved.   This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   IBM - Initial API and implementation
*   E.D.Willink - Lexer and Parser refactoring to support extensibility and flexible error handling
*
* </copyright>
*
* $Id: QvtOpLexerprs.java,v 1.72 2009/04/22 09:54:48 aigdalov Exp $
*/
/**
* <copyright>
*
* Copyright (c) 2006-2008 Borland Inc.
* All rights reserved.   This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Borland - Initial API and implementation
*
* </copyright>
*
* $Id: QvtOpLexerprs.java,v 1.72 2009/04/22 09:54:48 aigdalov Exp $
*/

package org.eclipse.m2m.internal.qvt.oml.cst.parser;

public class QvtOpLexerprs implements lpg.lpgjavaruntime.ParseTable, QvtOpLexersym {

    public interface IsKeyword {
        public final static byte isKeyword[] = {0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0
        };
    };
    public final static byte isKeyword[] = IsKeyword.isKeyword;
    public final boolean isKeyword(int index) { return isKeyword[index] != 0; }

    public interface BaseCheck {
        public final static byte baseCheck[] = {0,
            1,1,3,3,3,1,1,1,1,1,
            5,1,1,1,1,1,1,1,1,1,
            1,2,2,2,1,1,1,1,2,1,
            1,1,2,1,1,2,4,1,2,1,
            1,1,2,2,3,2,2,0,1,2,
            2,2,1,2,1,2,3,2,3,3,
            1,1,1,1,1,1,1,1,1,1,
            1,1,1,1,1,1,1,1,1,1,
            1,1,1,1,1,1,1,1,1,1,
            1,1,1,1,1,1,1,1,1,1,
            1,1,1,1,1,1,1,1,1,1,
            1,1,1,1,1,1,1,1,1,1,
            1,1,1,1,1,1,1,1,1,1,
            1,1,1,1,1,1,1,1,1,1,
            1,1,1,1,1,1,1,1,1,1,
            1,1,1,1,2,3,1,1,1,1,
            1,2,1,2,2,2,1,1,1,1,
            1,1,1,1,1,1,1,1,1,1,
            1,1,1,1,1,1,1,1,1,1,
            1,1,1,1,1,1,1,1,1,1,
            1,1,1,1,1,1,1,1,1,1,
            1,1,1,1,1,1,1,1,1,1,
            1,1,1,1,1,1,1,1,1,1,
            1,1,1,1,1,1,1,1,1,1,
            1,1,1,1,1,1,1,1,1,1,
            1,1,1,1,1,1,1,1,1,1,
            1,2,1,1,1,1,1,1,1,1,
            1,1,1,1,1,1,1,1,1,1,
            1,1,1,1,1,1,1,2,1,2,
            1,2,0,1,3,3,3,3,3,3,
            4,4,3,3,2,2,1,1,2,2,
            2,3,1,3,1,1,1,1,1,1,
            1,3,0,1,2
        };
    };
    public final static byte baseCheck[] = BaseCheck.baseCheck;
    public final int baseCheck(int index) { return baseCheck[index]; }
    public final static byte rhs[] = baseCheck;
    public final int rhs(int index) { return rhs[index]; };

    public interface BaseAction {
        public final static char baseAction[] = {
            20,20,20,20,20,20,20,20,20,20,
            20,20,20,20,20,20,20,20,20,20,
            20,20,20,20,20,20,20,20,20,20,
            20,20,20,20,20,20,20,20,20,20,
            20,22,23,23,23,27,27,27,27,28,
            28,26,26,7,7,38,38,30,14,14,
            14,10,10,10,10,10,2,2,2,2,
            3,3,3,3,3,3,3,3,3,3,
            3,3,3,3,3,3,3,3,3,3,
            3,3,3,3,3,3,4,4,4,4,
            4,4,4,4,4,4,4,4,4,4,
            4,4,4,4,4,4,4,4,4,4,
            4,4,1,1,1,1,1,1,1,1,
            1,1,40,40,40,40,40,40,40,40,
            41,41,41,41,41,41,41,41,41,41,
            41,41,39,39,42,42,42,16,16,43,
            43,29,29,12,12,12,12,32,32,32,
            32,32,32,32,32,32,32,32,32,32,
            32,32,32,32,32,32,32,32,32,32,
            32,32,32,32,32,32,32,17,17,17,
            17,17,17,17,17,17,17,17,17,17,
            17,17,17,17,17,17,17,17,17,17,
            17,17,17,17,17,17,5,5,5,5,
            5,5,5,5,5,5,5,5,5,5,
            5,5,5,5,5,5,5,5,5,5,
            5,5,5,5,18,18,8,8,33,33,
            33,33,6,15,15,15,15,31,31,31,
            31,31,31,31,31,34,34,34,34,19,
            19,19,19,9,9,9,9,9,21,35,
            35,11,11,13,13,24,24,24,24,24,
            24,24,24,24,25,20,20,20,20,20,
            20,20,20,20,20,9,9,9,19,19,
            19,19,20,36,36,26,905,510,496,496,
            496,1380,54,443,1391,54,511,1416,498,507,
            512,512,512,512,512,512,337,2,6,7,
            8,9,437,1402,54,373,456,512,512,607,
            513,513,513,513,513,513,1433,513,513,1191,
            409,1327,490,1413,402,448,607,513,513,513,
            513,513,513,162,513,513,1084,409,1432,491,
            307,422,422,422,422,422,422,1429,422,422,
            1430,422,706,290,290,290,290,290,290,806,
            292,292,292,292,292,292,1434,292,292,296,
            290,290,407,292,292,292,292,292,292,1252,
            292,292,1,47,47,47,47,203,52,52,
            52,52,47,1330,54,1414,432,47,1118,47,
            1007,262,262,262,262,262,1436,44,1435,466,
            461,102,45,45,45,45,1347,331,52,52,
            43,45,466,331,1358,353,45,1328,45,1418,
            54,353,1442,262,262,1107,334,503,503,503,
            1000,1437,334,1439,1452,1454,1438,503,1180,165,
            164,164,164,1255,165,164,164,164,1369,479,
            1455,1456,1248,1458,518,479,518,518
        };
    };
    public final static char baseAction[] = BaseAction.baseAction;
    public final int baseAction(int index) { return baseAction[index]; }
    public final static char lhs[] = baseAction;
    public final int lhs(int index) { return lhs[index]; };

    public interface TermCheck {
        public final static byte termCheck[] = {0,
            0,1,2,3,4,5,6,7,8,9,
            10,11,12,13,14,15,16,17,18,19,
            20,21,22,23,24,25,26,27,28,29,
            30,31,32,33,34,35,36,37,38,39,
            40,41,42,43,44,45,46,47,48,49,
            50,51,52,53,54,55,56,57,58,59,
            60,61,62,63,64,65,66,67,68,69,
            70,71,72,73,74,75,76,77,78,79,
            80,81,82,83,84,85,86,87,88,89,
            90,91,92,93,94,95,96,97,98,99,
            100,0,1,2,3,4,5,6,7,8,
            9,10,11,12,13,14,15,16,17,18,
            19,20,21,22,23,24,25,26,27,28,
            29,30,31,32,33,34,35,36,37,38,
            39,40,41,42,43,44,45,46,47,48,
            49,50,51,52,53,54,55,56,57,58,
            59,60,61,62,63,64,65,66,67,68,
            69,70,71,72,73,74,75,76,77,78,
            79,80,81,82,83,84,85,86,87,88,
            89,90,91,92,93,94,95,96,97,98,
            99,100,0,1,2,3,4,5,6,7,
            8,9,10,11,12,13,14,15,16,17,
            18,19,20,21,22,23,24,25,26,27,
            28,29,30,31,32,33,34,35,36,37,
            38,39,40,41,42,43,44,45,46,47,
            48,49,50,51,52,53,54,55,56,57,
            58,59,60,61,62,63,64,65,66,67,
            68,69,70,71,72,73,74,75,76,77,
            78,79,80,81,82,83,84,85,86,87,
            88,89,90,91,92,0,0,95,96,97,
            98,99,100,0,0,103,0,1,2,3,
            4,5,6,7,8,9,10,11,12,13,
            14,15,16,17,18,19,20,21,22,23,
            24,25,26,27,28,29,30,31,32,33,
            34,35,36,37,38,39,40,41,42,43,
            44,45,46,47,48,49,50,51,52,53,
            54,55,56,57,58,59,60,61,62,63,
            64,65,66,67,68,69,70,71,72,73,
            74,75,76,77,78,79,80,81,82,83,
            84,85,86,87,88,89,90,91,92,93,
            94,95,96,97,98,99,0,1,2,3,
            4,5,6,7,8,9,10,11,12,13,
            14,15,16,17,18,19,20,21,22,23,
            24,25,26,27,28,29,30,31,32,33,
            34,35,36,37,38,39,40,41,42,43,
            44,45,46,47,48,49,50,51,52,53,
            54,55,56,57,58,59,60,61,62,63,
            64,65,66,67,68,69,70,71,72,73,
            74,75,76,77,78,79,80,81,82,83,
            84,85,86,87,88,89,90,91,92,93,
            94,95,96,97,98,99,0,1,2,3,
            4,5,6,7,8,9,10,11,12,13,
            14,15,16,17,18,19,20,21,22,23,
            24,25,26,27,28,29,30,31,32,33,
            34,35,36,37,38,39,40,41,42,43,
            44,45,46,47,48,49,50,51,52,53,
            54,55,56,57,58,59,60,61,62,63,
            64,65,66,67,68,69,70,71,72,73,
            74,75,76,77,0,79,80,81,82,83,
            84,85,86,87,88,89,90,91,92,93,
            94,95,96,97,98,99,0,1,2,3,
            4,5,6,7,8,9,10,11,12,13,
            14,15,16,17,18,19,20,21,22,23,
            24,25,26,27,28,29,30,31,32,33,
            34,35,36,37,38,39,40,41,42,43,
            44,45,46,47,48,49,50,51,52,53,
            54,55,56,57,58,59,60,61,62,63,
            64,65,66,67,68,69,70,71,72,73,
            74,75,76,77,78,79,80,81,82,83,
            84,85,86,87,88,89,90,91,92,93,
            94,95,96,97,98,0,1,2,3,4,
            5,6,7,8,9,10,11,12,13,14,
            15,16,17,18,19,20,21,22,23,24,
            25,26,27,28,29,30,31,32,33,34,
            35,36,37,38,39,40,41,42,43,44,
            45,46,47,48,49,50,51,52,53,54,
            55,56,57,58,59,60,61,62,63,64,
            65,66,67,68,69,70,71,72,73,74,
            75,76,77,0,79,80,81,82,83,84,
            85,86,87,88,89,90,91,92,93,94,
            95,96,97,98,99,0,1,2,3,4,
            5,6,7,8,9,10,11,12,13,14,
            15,16,17,18,19,20,21,22,23,24,
            25,26,27,28,29,30,31,32,33,34,
            35,36,37,38,39,40,41,42,43,44,
            45,46,47,48,49,50,51,52,53,54,
            55,56,57,58,59,60,61,62,63,64,
            65,66,67,68,69,70,71,72,73,74,
            75,76,77,78,79,80,81,82,83,84,
            85,86,87,88,89,90,91,92,93,94,
            95,96,97,98,0,1,2,3,4,5,
            6,7,8,9,10,11,12,13,14,15,
            16,17,18,19,20,21,22,23,24,25,
            26,27,28,29,30,31,32,33,34,35,
            36,37,38,39,40,41,42,43,44,45,
            46,47,48,49,50,51,52,53,54,55,
            56,57,58,59,60,61,62,63,64,65,
            66,67,68,69,70,71,72,73,0,75,
            76,77,78,79,80,81,82,83,84,85,
            86,87,88,89,90,91,92,93,94,0,
            0,0,0,99,100,101,0,1,2,3,
            4,5,6,7,8,9,10,11,12,13,
            14,15,16,17,18,19,20,21,22,23,
            24,25,26,27,28,29,30,31,32,33,
            34,35,36,37,38,39,40,41,42,43,
            44,45,46,47,48,49,50,51,52,53,
            54,55,56,57,58,59,60,61,62,63,
            64,65,66,67,68,69,70,71,72,73,
            74,75,76,0,78,79,80,81,82,83,
            84,85,86,87,88,89,90,91,92,16,
            101,95,96,97,98,99,0,1,2,3,
            4,5,6,7,8,9,10,0,12,13,
            14,15,16,17,18,19,20,21,22,23,
            24,25,26,27,28,29,30,31,32,33,
            34,35,36,37,38,39,40,41,42,43,
            44,45,46,47,48,49,50,51,52,53,
            54,55,56,57,58,59,60,61,62,63,
            64,65,66,67,68,69,70,71,72,0,
            1,2,3,4,5,6,7,8,9,10,
            0,12,13,14,15,78,0,18,19,20,
            21,22,23,24,25,26,27,28,29,30,
            31,32,33,34,35,36,37,38,39,40,
            41,42,43,44,45,46,47,48,49,50,
            51,52,53,54,55,56,57,58,59,60,
            61,62,63,64,65,66,67,0,0,0,
            0,0,0,74,0,1,2,3,4,5,
            6,7,8,9,10,75,12,13,14,15,
            0,0,18,19,20,21,22,23,24,25,
            26,27,28,29,30,31,32,33,34,35,
            36,37,38,39,40,41,42,43,44,45,
            46,47,48,49,50,51,52,53,54,55,
            56,57,58,59,60,61,62,63,64,65,
            66,67,71,72,0,78,0,0,74,0,
            1,2,3,4,5,6,7,8,9,10,
            14,12,13,16,0,16,0,1,2,3,
            4,5,6,7,8,9,10,0,1,2,
            3,4,5,6,7,8,9,10,0,1,
            2,3,4,5,6,7,8,9,10,0,
            1,2,3,4,5,6,7,8,9,10,
            0,1,2,3,4,5,6,7,8,9,
            10,0,1,2,3,4,5,6,7,8,
            9,10,0,0,68,0,70,0,1,2,
            3,4,5,6,7,8,9,10,0,0,
            17,0,0,0,0,0,0,0,0,11,
            11,0,11,11,11,17,17,12,13,15,
            12,0,11,0,0,0,0,0,0,0,
            0,0,11,0,11,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,
            0,68,0,0,0,73,0,0,0,77,
            0,0,0,0,0,0,0,69,0,0,
            0,0,0,0,0,93,94,76,0,0,
            0,0,100,0,0,0,0,102,0,0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,101,0,102,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,102,102,101,0,
            0
        };
    };
    public final static byte termCheck[] = TermCheck.termCheck;
    public final int termCheck(int index) { return termCheck[index]; }

    public interface TermAction {
        public final static char termAction[] = {0,
            518,565,565,565,565,565,565,565,565,565,
            565,565,565,565,565,565,565,565,565,565,
            565,565,565,565,565,565,565,565,565,565,
            565,565,565,565,565,565,565,565,565,565,
            565,565,565,565,565,565,565,565,565,565,
            565,565,565,565,565,565,565,565,565,565,
            565,565,565,565,565,565,565,565,565,565,
            565,564,461,565,565,565,565,565,565,565,
            565,565,565,565,565,565,565,565,565,565,
            565,565,565,565,565,565,565,565,565,565,
            565,518,563,563,563,563,563,563,563,563,
            563,563,563,563,563,563,563,563,563,563,
            563,563,563,563,563,563,563,563,563,563,
            563,563,563,563,563,563,563,563,563,563,
            563,563,563,563,563,563,563,563,563,563,
            563,563,563,563,563,563,563,563,563,563,
            563,563,563,563,563,563,563,563,563,563,
            563,563,529,568,563,563,563,563,563,563,
            563,563,563,563,563,563,563,563,563,563,
            563,563,563,563,563,563,563,563,563,563,
            563,563,10,570,570,570,570,570,570,570,
            570,570,570,570,570,570,570,570,570,570,
            570,570,570,570,570,570,570,570,570,570,
            570,570,570,570,570,570,570,570,570,570,
            570,570,570,570,570,570,570,570,570,570,
            570,570,570,570,570,570,570,570,570,570,
            570,570,570,570,570,570,570,570,570,570,
            570,570,570,570,570,570,570,570,570,570,
            570,570,570,570,570,570,570,570,570,570,
            570,570,570,570,570,48,518,570,570,570,
            570,570,570,518,518,570,518,422,422,422,
            422,422,422,422,422,422,422,422,422,422,
            422,422,422,422,422,422,422,422,422,422,
            422,422,422,422,422,422,422,422,422,422,
            422,422,422,422,422,422,422,422,422,422,
            422,422,422,422,422,422,422,422,422,422,
            422,422,422,422,422,422,422,422,422,422,
            422,422,422,422,422,422,422,422,422,422,
            422,422,422,422,422,422,422,422,422,422,
            422,422,422,422,422,422,422,422,422,422,
            422,422,422,422,450,806,518,810,810,810,
            810,810,810,810,810,810,810,810,810,810,
            810,810,810,810,810,810,810,810,810,810,
            810,810,810,810,810,810,810,810,810,810,
            810,810,810,810,810,810,810,810,810,810,
            810,810,810,810,810,810,810,810,810,810,
            810,810,810,810,810,810,810,810,810,810,
            810,810,810,810,810,810,810,810,810,810,
            810,810,810,810,810,810,810,810,810,810,
            810,810,810,810,810,810,810,810,810,810,
            810,810,810,810,450,521,323,512,512,512,
            512,512,512,512,512,512,512,512,512,512,
            512,512,512,512,512,512,512,512,512,512,
            512,512,512,512,512,512,512,512,512,512,
            512,512,512,512,512,512,512,512,512,512,
            512,512,512,512,512,512,512,512,512,512,
            512,512,512,512,512,512,512,512,512,512,
            512,512,512,512,512,512,512,512,512,512,
            512,512,512,512,518,512,512,512,512,512,
            512,512,512,512,512,512,512,512,512,512,
            512,512,512,512,450,512,293,513,513,513,
            513,513,513,513,513,513,513,513,513,513,
            513,513,513,513,513,513,513,513,513,513,
            513,513,513,513,513,513,513,513,513,513,
            513,513,513,513,513,513,513,513,513,513,
            513,513,513,513,513,513,513,513,513,513,
            513,513,513,513,513,513,513,513,513,513,
            513,513,513,513,513,513,513,513,513,513,
            513,513,513,513,513,513,513,513,513,513,
            513,513,513,513,513,513,513,513,513,513,
            513,513,513,513,450,324,808,808,808,808,
            808,808,808,808,808,808,808,808,808,808,
            808,808,808,808,808,808,808,808,808,808,
            808,808,808,808,808,808,808,808,808,808,
            808,808,808,808,808,808,808,808,808,808,
            808,808,808,808,808,808,808,808,808,808,
            808,808,808,808,808,808,808,808,808,808,
            808,808,808,808,808,808,808,808,808,808,
            808,808,808,518,808,808,808,808,808,808,
            808,808,808,808,808,808,808,808,808,808,
            808,808,808,450,808,294,810,810,810,810,
            810,810,810,810,810,810,810,810,810,810,
            810,810,810,810,810,810,810,810,810,810,
            810,810,810,810,810,810,810,810,810,810,
            810,810,810,810,810,810,810,810,810,810,
            810,810,810,810,810,810,810,810,810,810,
            810,810,810,810,810,810,810,810,810,810,
            810,810,810,810,810,810,810,810,810,810,
            810,810,810,810,810,810,810,810,810,810,
            810,810,810,810,810,810,810,810,810,810,
            810,810,810,450,518,510,510,510,510,510,
            510,510,510,510,510,539,496,496,496,496,
            386,400,496,496,496,496,496,496,496,496,
            496,496,496,496,496,496,496,496,496,496,
            496,496,496,496,496,496,496,496,496,496,
            496,496,496,496,496,496,496,496,496,496,
            496,496,496,496,496,496,496,496,496,496,
            496,496,445,397,416,429,533,511,518,369,
            388,511,339,548,366,371,359,831,552,545,
            546,543,544,558,549,535,536,511,511,518,
            518,518,518,390,511,376,518,780,780,780,
            780,780,780,780,780,780,780,780,780,780,
            780,780,780,780,780,780,780,780,780,780,
            780,780,780,780,780,780,780,780,780,780,
            780,780,780,780,780,780,780,780,780,780,
            780,780,780,780,780,780,780,780,780,780,
            780,780,780,780,780,780,780,780,780,780,
            780,780,780,780,780,780,780,780,780,780,
            780,780,780,35,780,780,780,780,780,780,
            780,780,780,780,780,780,780,780,780,477,
            523,780,780,780,780,780,518,334,334,334,
            334,334,334,334,334,334,334,518,503,503,
            503,503,822,494,503,503,503,503,503,503,
            503,503,503,503,503,503,503,503,503,503,
            503,503,503,503,503,503,503,503,503,503,
            503,503,503,503,503,503,503,503,503,503,
            503,503,503,503,503,503,503,503,503,503,
            503,503,503,503,815,495,814,817,816,1,
            683,683,683,683,683,683,683,683,683,683,
            38,682,682,682,682,840,518,682,682,682,
            682,682,682,682,682,682,682,682,682,682,
            682,682,682,682,682,682,682,682,682,682,
            682,682,682,682,682,682,682,682,682,682,
            682,682,682,682,682,682,682,682,682,682,
            682,682,682,682,682,682,682,289,518,518,
            518,16,518,684,295,683,683,683,683,683,
            683,683,683,683,683,557,682,682,682,682,
            518,518,682,682,682,682,682,682,682,682,
            682,682,682,682,682,682,682,682,682,682,
            682,682,682,682,682,682,682,682,682,682,
            682,682,682,682,682,682,682,682,682,682,
            682,682,682,682,682,682,682,682,682,682,
            682,682,843,419,518,324,307,36,684,6,
            572,572,572,572,572,572,572,572,572,572,
            458,466,466,830,518,485,518,331,331,331,
            331,331,331,331,331,331,331,518,353,353,
            353,353,353,353,353,353,353,353,518,479,
            479,479,479,479,479,479,479,479,479,58,
            572,572,572,572,572,572,572,572,572,572,
            57,572,572,572,572,572,572,572,572,572,
            572,60,572,572,572,572,572,572,572,572,
            572,572,12,14,508,518,474,59,572,572,
            572,572,572,572,572,572,572,572,20,19,
            547,32,308,13,518,7,518,163,518,541,
            540,33,823,827,824,542,829,466,466,493,
            555,303,832,300,53,161,518,291,518,518,
            518,518,820,518,819,518,518,518,518,518,
            518,518,518,518,518,518,518,518,518,518,
            518,569,518,518,518,680,518,518,518,680,
            518,518,518,518,518,518,518,828,518,518,
            518,518,518,518,518,680,680,482,518,518,
            518,518,680,518,518,518,518,517,518,518,
            518,518,518,518,518,518,518,518,518,518,
            518,518,518,518,518,518,518,522,518,1,
            518,518,518,518,518,518,518,518,518,518,
            518,518,518,518,518,518,6,12,294
        };
    };
    public final static char termAction[] = TermAction.termAction;
    public final int termAction(int index) { return termAction[index]; }
    public final int asb(int index) { return 0; }
    public final int asr(int index) { return 0; }
    public final int nasb(int index) { return 0; }
    public final int nasr(int index) { return 0; }
    public final int terminalIndex(int index) { return 0; }
    public final int nonterminalIndex(int index) { return 0; }
    public final int scopePrefix(int index) { return 0;}
    public final int scopeSuffix(int index) { return 0;}
    public final int scopeLhs(int index) { return 0;}
    public final int scopeLa(int index) { return 0;}
    public final int scopeStateSet(int index) { return 0;}
    public final int scopeRhs(int index) { return 0;}
    public final int scopeState(int index) { return 0;}
    public final int inSymb(int index) { return 0;}
    public final String name(int index) { return null; }
    public final int getErrorSymbol() { return 0; }
    public final int getScopeUbound() { return 0; }
    public final int getScopeSize() { return 0; }
    public final int getMaxNameLength() { return 0; }

    public final static int
           NUM_STATES        = 51,
           NT_OFFSET         = 103,
           LA_STATE_OFFSET   = 843,
           MAX_LA            = 1,
           NUM_RULES         = 325,
           NUM_NONTERMINALS  = 43,
           NUM_SYMBOLS       = 146,
           SEGMENT_SIZE      = 8192,
           START_STATE       = 326,
           IDENTIFIER_SYMBOL = 0,
           EOFT_SYMBOL       = 102,
           EOLT_SYMBOL       = 104,
           ACCEPT_ACTION     = 517,
           ERROR_ACTION      = 518;

    public final static boolean BACKTRACK = false;

    public final int getNumStates() { return NUM_STATES; }
    public final int getNtOffset() { return NT_OFFSET; }
    public final int getLaStateOffset() { return LA_STATE_OFFSET; }
    public final int getMaxLa() { return MAX_LA; }
    public final int getNumRules() { return NUM_RULES; }
    public final int getNumNonterminals() { return NUM_NONTERMINALS; }
    public final int getNumSymbols() { return NUM_SYMBOLS; }
    public final int getSegmentSize() { return SEGMENT_SIZE; }
    public final int getStartState() { return START_STATE; }
    public final int getStartSymbol() { return lhs[0]; }
    public final int getIdentifierSymbol() { return IDENTIFIER_SYMBOL; }
    public final int getEoftSymbol() { return EOFT_SYMBOL; }
    public final int getEoltSymbol() { return EOLT_SYMBOL; }
    public final int getAcceptAction() { return ACCEPT_ACTION; }
    public final int getErrorAction() { return ERROR_ACTION; }
    public final boolean isValidForParser() { return isValidForParser; }
    public final boolean getBacktrack() { return BACKTRACK; }

    public final int originalState(int state) { return 0; }
    public final int asi(int state) { return 0; }
    public final int nasi(int state) { return 0; }
    public final int inSymbol(int state) { return 0; }

    public final int ntAction(int state, int sym) {
        return baseAction[state + sym];
    }

    public final int tAction(int state, int sym) {
        int i = baseAction[state],
            k = i + sym;
        return termAction[termCheck[k] == sym ? k : i];
    }
    public final int lookAhead(int la_state, int sym) {
        int k = la_state + sym;
        return termAction[termCheck[k] == sym ? k : la_state];
    }
}
