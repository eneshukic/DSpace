-- Table: public.rate

-- DROP TABLE public.rate;

CREATE TABLE public.rate
(
    rate_id integer NOT NULL,
    rate_grade character varying(50) COLLATE pg_catalog."default",
    calculation_unit character varying(50) COLLATE pg_catalog."default",
    rate_description character varying(1000) COLLATE pg_catalog."default",
    date_modified timestamp without time zone,
    modified_by uuid,
    price numeric(7, 2) DEFAULT 0,
    CONSTRAINT rate_pkey PRIMARY KEY (rate_id),
    CONSTRAINT "UC_rateGrade" UNIQUE (rate_grade),
    CONSTRAINT "fkModifiedBy" FOREIGN KEY (modified_by)
        REFERENCES public.eperson (uuid) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE public.rate
    OWNER to dspace;
COMMENT ON TABLE public.rate
    IS 'Custom table to store rate grades and prices.  Rate grades will be applied to metadata fields';

COMMENT ON CONSTRAINT "UC_rateGrade" ON public.rate
    IS 'One active rate grade';
	
CREATE SEQUENCE public.rate_seq
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 99999999
    CACHE 1;

ALTER SEQUENCE public.rate_seq
    OWNER TO postgres;