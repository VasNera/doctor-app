INSERT INTO capabilities(name, description)
VALUES ('CREATE_TIMESLOT', 'Create timeslots');

INSERT INTO roles_capabilities (role_id, capability_id)
SELECT (SELECT id FROM roles WHERE name = 'DOCTOR'),
       (SELECT id FROM capabilities WHERE name = 'CREATE_TIMESLOT');