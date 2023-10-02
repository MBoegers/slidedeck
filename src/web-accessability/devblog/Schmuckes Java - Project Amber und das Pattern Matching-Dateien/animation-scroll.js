const animatedElements = document.querySelectorAll('.headline-module, .component.contacts, .full-width-container--contentwrapper h1, .full-width-container--contentwrapper h2, .full-width-container--contentwrapper h3, .full-width-container--contentwrapper h4, .full-width-container--contentwrapper h5, .full-width-container--contentwrapper h6, .text-image h1, .text-image h2, .text-image h3, .text-image h4, .text-image h5, .text-image h6, .imageheader--heading, .adesso-btn, .adesso-outline-btn');
const observer = new IntersectionObserver(entries => {
	entries.forEach(entry => {
		entry.target.classList.toggle("fadeInDown", entry.isIntersecting)
		if(entry.isIntersecting) observer.unobserve(entry.target);
	})
	},
	{
		rootMargin:"-100px"
	}
);
animatedElements.forEach(element => {
	element.classList.add('animated');
	element.style.opacity = 0;
	try {
		observer.observe(element);
	} catch (err) {
		console.error(err);
	}
});
