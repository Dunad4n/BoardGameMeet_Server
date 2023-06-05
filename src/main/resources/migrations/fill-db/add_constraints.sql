alter table public.event
add foreign key (host) references public.person(person_id)

alter table public.item
add foreign key (event_id) references public.event(event_id)

alter table public.message
add foreign key (event) references public.event(event_id)

alter table public.message
add foreign key (person_id) references public.person(person_id)

