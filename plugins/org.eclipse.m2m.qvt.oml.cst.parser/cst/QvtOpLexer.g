--/**
-- * <copyright>
-- *
-- * Copyright (c) 2006, 2007 Borland Inc.
-- * All rights reserved.   This program and the accompanying materials
-- * are made available under the terms of the Eclipse Public License v1.0
-- * which accompanies this distribution, and is available at
-- * http://www.eclipse.org/legal/epl-v10.html
-- *
-- * Contributors:
-- *   Borland - Initial API and implementation
-- *
-- * </copyright>
-- *
-- * $Id: QvtOpLexer.g,v 1.4 2009/01/14 13:04:41 aigdalov Exp $
-- */
--
-- The QVT Lexer
--

%options escape=$
%options la=2
%options fp=QvtOpLexer,prefix=Char_
%options single-productions
%options noserialize
%options template=../lpg/LexerTemplateD.g
%options filter=QvtOpKWLexer.g
%options export_terminals=("QVTParsersym.java", "TK_")
%options include_directory=".;../lpg"


$Define

	--
	-- Definition of macro used in the included file OCLLexer.g
	--
	$OCLKWLexer /.QvtOpKWLexer./

$End

$Include
	LexerBasicMap.g
$End

$Headers
	/.
		/*
	./
$End


$Import
	OCLLexer.g

$DropRules

	Identifier -> QuotedName

$End

$Globals
    /.import org.eclipse.ocl.parser.OCLKWLexer;
    ./
$End

$Headers
	/.
		*/
	./
$End

$Notice
	/./**
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
 * $Id: QvtOpLexer.g,v 1.4 2009/01/14 13:04:41 aigdalov Exp $
 */
	./
$End

$Export

	QUOTE_STRING_LITERAL
	ADD_ASSIGN
	RESET_ASSIGN
	AT_SIGN
	EXCLAMATION_MARK
	NOT_EQUAL_EXEQ
	STEREOTYPE_QUALIFIER_OPEN
	STEREOTYPE_QUALIFIER_CLOSE
	MULTIPLICITY_RANGE
	TILDE_SIGN
	COLONCOLONEQUAL
	
$End

$Rules

	Token ::= QuotedName
		/.$BeginAction
					makeToken($_QUOTE_STRING_LITERAL);
		  $EndAction
		./

	Token ::= ':' '='
		/.$BeginAction
					makeToken($_RESET_ASSIGN);
		  $EndAction
		./

	Token ::= '+' '='
		/.$BeginAction
					makeToken($_ADD_ASSIGN);
		  $EndAction
		./

	Token ::= '@'
		/.$BeginAction
					makeToken($_AT_SIGN);
		  $EndAction
		./

	Token ::= '!'
		/.$BeginAction
					makeToken($_EXCLAMATION_MARK);
		  $EndAction
		./

	Token ::= '!' '='
		/.$BeginAction
					makeToken($_NOT_EQUAL_EXEQ);
		  $EndAction
		./

	Token ::= '<' '<'
		/.$BeginAction
					makeToken($_STEREOTYPE_QUALIFIER_OPEN);
		  $EndAction
		./

	Token ::= '>' '>'
		/.$BeginAction
					makeToken($_STEREOTYPE_QUALIFIER_CLOSE);
		  $EndAction
		./

	Token ::= '.' '.' '.'
		/.$BeginAction
					makeToken($_MULTIPLICITY_RANGE);
		  $EndAction
		./

	Token ::= '~'
		/.$BeginAction
					makeToken($_TILDE_SIGN);
		  $EndAction
		./

	Token ::= ':' ':' '='
		/.$BeginAction
					makeToken($_COLONCOLONEQUAL);
		  $EndAction
		./


	NotSQ -> HT
	NotSQ -> LF
	NotSQ -> CR
	
$End
