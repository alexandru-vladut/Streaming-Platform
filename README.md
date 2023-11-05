# Streaming-Platform
> _Backend implementation of a movie streaming platform with similar features to Netflix or HBO Max._

## Structura

Platforma va funcționa pe principiul unui sistem de fișiere - în funcție de pagină curentă pe care se află, utilizatorul:
- poate înainta către un set specific de pagini (permise de către sistemul de fișiere).
- poate folosi funcționalități specifice paginii.
- se poate întoarce pe pagină anterioară.

Sistemul de fișiere va avea următoarea structură:

![filesystem](/assets/filesystem_explained.png)

> [!IMPORTANT]
> **Indentarea** sistemului de fișiere este cea care determina dacă trecerea de pe o pagină pe altă sau folosirea unei funcționalități pe o pagină specifică sunt permise sau nu.

O reprezentare mai detaliată a structurii, incluzând toate navigarile posibile între pagini:

![filesystem](/assets/diagram.png)

## Funcționalități (tipuri de acțiuni)
Platforma suportă 4 tipuri de acțiuni:
1. `change page` -> Realizată de către utilizator. Reprezintă navigarea către o nouă pagină (primită că parametru). Paginile disponibile sunt cele prezentate în structura.
2. `on page` -> Realizate de către utilizator. Reprezintă realizarea unei acțiuni specifice în cadrul unei pagini (ex: acțiunile de _Search_ sau _Filter_ în cadrul paginii _Movies_, pentru a filtra lista de filme).
3. `back` -> Realizată de către utilizator. Reprezintă navigarea pe pagină anterioară.
4. `database` -> Realizată de către administratorul platformei. Reprezintă adăugarea/ștergerea filmelor în baza de date. Se poate realiza oricând, indiferent de indentarea structurii platformei.

> [!NOTE]
> Suplimentar față de acțiunile `on page` prezentate în structura, există și acțiunea _Subscribe_ prin care un utilizator se poate abona la un anumit gen de filme.

## Date de intrare
Format JSON, conține 3 categorii de obiecte: utilizatori, filme și acțiuni.

**_Utilizatorii_** vor fi cei care sunt deja înregistrați în platforma la pornire (se pot înregistra utilizatori noi).

**_Filmele_** vor fi cele care sunt deja adăugate pe platforma la pornire (se pot adaugă filme noi, dar se pot și șterge cele existente).

**_Actiunile_** vor reprezenta totalitatea activităților utilizatorilor pe platforma în cadrul unei rulări a programului, având în vedere absența unei interfețe grafice.

> **_Users:_**
```
"users": [
    {
      "credentials": {
        "name": "Eduard",
        "password": "secret",
        "accountType": "standard",
        "country": "Romania",
        "balance": "200"
      }
    }
]
```

> **_Movies:_**
```
"movies": [
    {
        "name": "John Wick: Chapter 3 - Parabellum",
      	"year": "2019",
      	"duration": 131,
      	"genres": [
      	    "Action",
      	    "Thriller",
      	    "Crime"
      	],
      	"actors": [
      	    "Laurence Fishburne",
      	    "Halle Berry",
      	    "Keanu Reeves"
      	],
      	"countriesBanned": [
      	    "Russia",
      	    "Austria"
      	]
    }
]
```

> **_Acțiunea de tip „change page”_**
```
{
    "type": "change page",
    "page": "login"
}
```

> **_Acțiunea de tip „change page” - excepție pentru See Details_**
```
{
    "type": "change page",
    "page": "see details",
    "movie": "movie title"
}
```

> **_Acțiunea de „register”_**
```
{
      "type": "on page",
      "page": "register",
      "feature": "register",
      "credentials": {
        "name": "Mihnea",
        "password": "acoperisvasile",
        "accountType": "standard",
        "country": "Ireland",
        "balance": "300"
      }
}
```

> **_Acțiunea de „login”_**
```
{
      "type": "on page",
      "page": "login",
      "feature": "login",
      "credentials" : {
        "name": "Ramsi",
        "password": "numiplacevelea"
      }
}
```

> **_Acțiunea de „search”_**
``` 
{
      "type": "on page",
      "page": "movies",
      "feature": "search",
      "startsWith": "Iubire din serai"
}
```

> **_Acțiunea de „filter”_**
```
{
    "type": "on page",
    "page": "movies",
    "feature": "filter",
    "filters": {
        "sort": {
            "rating": "decreasing",
	    "duration": "decreasing"
	},
	"contains": {
	    "actors": [
	        "Keanu Reeves",
		"Actor 2"
	    ],
	    "genre": [
		"Comedy",
		"Drama"
	    ]
	}
    }
}
```

> **_Acțiunea de „buy tokens”_**
```
{
      "type": "on page",
      "page": "upgrades",
      "feature": "buy tokens",
      "count": "300"
}
```

> **_Acțiunea de „buy premium account”_**
```
{
      "type": "on page",
      "page": "upgrades",
      "feature": "buy premium account"
}
```

> **_Acțiunea de „Purchase”_**
```
{
      "type": "on page",
      "page": "see details",
      "feature": "purchase"
}
```

> **_Acțiunea de „Watch”_**
```
{
      "type": "on page",
      "page": "see details",
      "feature": "watch"
}
```

> **_Acțiunea de „Like”_**
```
{
      "type": "on page",
      "page": "see details",
      "feature": "like"
}
```

> **_Acțiunea de „Rate a movie”_**
```
{
      "type": "on page",
      "page": "see details",
      "feature": "rate",
      "rate": 4
}
```

> **_Acțiunea de „Subscribe”_**
```
{
      "type": "on page",
      "feature": "subscribe",
      "subscribedGenre": "Action"
}
```
