INSERT INTO public.role (name) VALUES ('ROLE_USER');
INSERT INTO public.role (name) VALUES ('ROLE_ADMIN');

INSERT INTO public.person (age, avatar_id, city, gender, name, nickname, password, secret_word) VALUES (20, 1, 'Voronezh', 'MALE', 'Denis', 'Doradura', '$2a$12$/CYs1FeY2NcXXee4z.Vk3uTt9J7DVF0JlPrdPjWAYuA2nFKg8ooCW', 'admin');
INSERT INTO public.person (age, avatar_id, city, gender, name, nickname, password, secret_word) VALUES (20, 2, 'Voronezh', 'MALE', 'Denis', 'Dunadan', '$2a$12$/CYs1FeY2NcXXee4z.Vk3uTt9J7DVF0JlPrdPjWAYuA2nFKg8ooCW', 'admin');
INSERT INTO public.person (age, avatar_id, city, gender, name, nickname, password, secret_word) VALUES (20, 3, 'Voronezh', 'MALE', 'Vanya', 'Vanius', '$2a$12$/CYs1FeY2NcXXee4z.Vk3uTt9J7DVF0JlPrdPjWAYuA2nFKg8ooCW', 'admin');
INSERT INTO public.person (age, avatar_id, city, gender, name, nickname, password, secret_word) VALUES (25, 4, 'Voronezh', 'MALE', 'Vadim', 'Vadim', '$2a$12$/CYs1FeY2NcXXee4z.Vk3uTt9J7DVF0JlPrdPjWAYuA2nFKg8ooCW', 'admin');

INSERT INTO public.person_roles (role_id, person_id) VALUES (2, 1);
INSERT INTO public.person_roles (role_id, person_id) VALUES (2, 2);
INSERT INTO public.person_roles (role_id, person_id) VALUES (2, 3);
INSERT INTO public.person_roles (role_id, person_id) VALUES (2, 4);

-- INSERT INTO public.person_roles (role_id, person_id) VALUES (1, 1);
-- INSERT INTO public.person_roles (role_id, person_id) VALUES (1, 2);
-- INSERT INTO public.person_roles (role_id, person_id) VALUES (1, 3);
-- INSERT INTO public.person_roles (role_id, person_id) VALUES (1, 4);