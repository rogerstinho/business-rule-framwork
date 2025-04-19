# The Basic COCOMO model is a straightforward way to estimate the effort needed for a software development project. It uses a simple mathematical formula to predict how many person-months of work are required based on the size of the project, measured in thousands of lines of code (KLOC).

It estimates effort and time required for development using the following expression:

E = a*(KLOC)b PM

Tdev = c*(E)d

Person required = Effort/ Time

Where,

E is effort applied in Person-Months

KLOC is the estimated size of the software product indicate in Kilo Lines of Code

Tdev is the development time in months

a, b, c are constants determined by the category of software project given in below table.

The above formula is used for the cost estimation of the basic COCOMO model and also is used in the subsequent models.
The constant values a, b, c, and d for the Basic Model for the different categories of the software projects are:

| Software Project | Then | a   | b    | c   | d    |
|------------------|------|-----|------|-----|------|
| ORGANIC          |      | 2.4 | 1.05 | 2.5 | 0.38 |
| SEMI_DETACHED    |      | 3.0 | 1.12 | 2.5 | 0.35 |
| EMBEDDED         |      | 3.6 | 1.20 | 2.5 | 0.32 |
