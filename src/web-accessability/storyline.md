# DevBlog

https://www.adesso.de/de/news/blog/schmuckes-java-project-amber-und-das-pattern-matching.jsp

WCAG Checker

## Adesso Xing Link
Its just a SVG containing no text at all!
add aria-label for example as aria-label="adesso at Xing"

Now ist no violation and can be accessed by blind people.
This small change fixed a lot of problems!

## Headings

h1 > h2 > h3 > h4 ist soll
wir haben h1 in h1 next level is h4 Oo

## Source code listings
1.4 > 1.4.3
Navigate to a listing, keep in mind DEV BLOG, with Code!

Some colors are reported as real bad, like the red Classes and Methods

https://www.whocanuse.com/?bg=f3f2f1&fg=0077aa&fs=16&fw=

let's tweak: https://geenes.app/editor/accessibility

background: f3f2f1
### Blue
text is: 0077aa (300)
text should: 006185 (400)

### Red
text is: dd4a68 (200)
text should: ae324d (400)

## pa11y
Okay, that was very clicky, do it with tools!
https://pa11y.org/

### Setup
```shell
brew install node
npm install -g pa11y
pa11y --reporter csv https://www.adesso.de/de/news/blog/index.jsp > adesso_devblog_pa11y-findings.csv
```

### pa11y Dashboard
https://user-images.githubusercontent.com/6110968/61603347-0bce1000-abf2-11e9-87b2-a53f91d315bb.jpg
https://user-images.githubusercontent.com/6110968/62183438-05851580-b30f-11e9-9bc4-b6a4823ae9e8.jpg
