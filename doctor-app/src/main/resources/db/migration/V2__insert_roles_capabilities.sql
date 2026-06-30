INSERT INTO roles(name)
VALUES
('ADMIN'),
('DOCTOR'),
('PATIENT');

INSERT INTO capabilities(name, description)
VALUES
('CREATE_DOCTOR','Create a new doctor'),
('UPDATE_DOCTOR','Update a doctor'),
('VIEW_DOCTOR','View a doctor'),
('VIEW_DOCTORS','View all doctors'),
('DELETE_DOCTOR','Delete a doctor'),
('VIEW_PATIENT','View a patient'),
('VIEW_PATIENTS','View all patients'),
('UPDATE_PATIENT','Update a patient'),
('DELETE_PATIENT','Delete a patient'),
('VIEW_TIMESLOT','View a timeslot'),
('VIEW_TIMESLOTS','View all timeslots'),
('VIEW_APPOINTMENT','View an appointment'),
('VIEW_APPOINTMENTS','View all appointments'),
('CREATE_APPOINTMENT','Create an appointment'),
('UPDATE_APPOINTMENT', 'Update an appointment'),
('DELETE_APPOINTMENT', 'Delete an appointment');


INSERT INTO roles_capabilities (role_id, capability_id)
SELECT (SELECT id FROM roles WHERE name = 'ADMIN'), c.id
FROM capabilities c
WHERE c.name IN  ('CREATE_DOCTOR', 'VIEW_DOCTOR', 'VIEW_DOCTORS', 'DELETE_DOCTOR',
     'UPDATE_DOCTOR', 'VIEW_PATIENT', 'VIEW_PATIENTS', 'VIEW_TIMESLOT', 'VIEW_TIMESLOTS',
    'CREATE_APPOINTMENT', 'VIEW_APPOINTMENT', 'VIEW_APPOINTMENTS', 'UPDATE_APPOINTMENT',
     'DELETE_APPOINTMENT');

INSERT INTO roles_capabilities (role_id, capability_id)
SELECT (SELECT id FROM roles WHERE name = 'DOCTOR'), c.id
FROM capabilities c
WHERE c.name IN  ('VIEW_DOCTOR', 'VIEW_PATIENT', 'VIEW_TIMESLOT', 'VIEW_TIMESLOTS',
          'VIEW_APPOINTMENT', 'VIEW_APPOINTMENTS', 'UPDATE_APPOINTMENT');

 INSERT INTO roles_capabilities (role_id, capability_id)
 SELECT (SELECT id FROM roles WHERE name = 'PATIENT'), c.id
 FROM capabilities c
 WHERE c.name IN ('VIEW_DOCTOR', 'VIEW_DOCTORS', 'VIEW_PATIENT',
     'DELETE_PATIENT', 'UPDATE_PATIENT', 'VIEW_TIMESLOT', 'VIEW_TIMESLOTS',
     'CREATE_APPOINTMENT', 'VIEW_APPOINTMENT', 'VIEW_APPOINTMENTS',
      'UPDATE_APPOINTMENT');