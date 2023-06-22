# DevBlog

https://www.adesso.de/de/news/blog/schmuckes-java-project-amber-und-das-pattern-matching.jsp

## Missing Labels

3.3 > 3.3.2

### Label für Input

label fehlt, können wir hinzufügen `<label id="adesso-search-label" for="adesso-search">Suche</label>`

### Label für button

aria-label does the trick für den Button `aria-label="Suche starten"`

## Link name

2.4 > 2.2.4

### Home link Adesso

* svg hat aria-label, gut
* a hat aber keinen

wir könnten ein aria-label oder aria-labeled by ergänzen.
Oder aber ein Title, der hilft allem nutzern.
`title="Adesso Landingpage"`

## Text contrast des Codes

1.4 > 1.4.3

### Source code listings

Farben passen nicht, lets check. Lass und mal den Contrast checker fauf machen.

https://www.whocanuse.com/?bg=f3f2f1&fg=0077aa&fs=16&fw=

dann tweaken wir: https://geenes.app/editor/accessibility

background: f3f2f1
text is: 0077aa (300)
text should: 006185 (400)

## Headings

1.3 > 1.3.3

### Nesting

h1 > h2 > h3 > h4 ist soll
wir haben h2 > h1 > h3? > h4

correct nesten und css anpassen s.d. aussehen wieder passt.
CSS ist Style und sollte nicht die Struktur beeinflussen!
