import React, { useState } from "react";
import type { UserAuthType } from "./UserAuth.types";
import { apiPost } from "../utils/api";
import { useFlash } from "../context/flash";
import { HttpRequestError } from "../errors/HttpRequestError";
import { useNavigate } from "react-router-dom";
import RegistrationPanel from "./RegistrationPanel";
import type { FieldError } from "./FieldError.types";


type FieldName = keyof UserAuthType;


type Touched = {
    fullName?: boolean;
  username?: boolean;
  password?: boolean;
  confirmPassword?: boolean;
}

const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

export const RegistrationPage = () =>{

    const [loading, setLoading] = useState(false);

    const navigate = useNavigate();

    const [valueState, setValueState] = useState<UserAuthType>({
        fullName: "",
        username: "",
        password: "",
        confirmPassword: "",
    });
    const [errorState, setErrorState] = useState<string>("");
    const [touched, setTuched] = useState<Touched>({});
    const [fieldError, setFieldsError] = useState<FieldError>({});
    const {showFlash} = useFlash();

    const setField = (name: FieldName, value: string) => {
        setValueState((prev) => ({...prev, [name]: value}));
    };

    const touchField = (name: FieldName) => {
        setTuched((prev) => ({...prev, [name]: true}));
    };

    const validate = (v: UserAuthType): FieldError =>{
        const nextField: FieldError = {};
        
        if(!v.fullName.trim()) nextField.fullName = "Zadej celé jméno";
        if(!v.username.trim()) nextField.username = "Zadej email";
        else if(!emailRegex.test(v.username.trim())) nextField.username = "Email nemá správný tvar"; 
        
        if(!v.password) nextField.password = "Zadej heslo";
        else if(v.password.length < 6) nextField.password = "Heslo musí mít alespoň 6 znaků";

        if(!v.confirmPassword) nextField.confirmPassword = "Znovu zadej heslo";
        else if(v.password !== v.confirmPassword) nextField.confirmPassword = "Hesla nesjou stejná";

        return nextField;
    }

    const validateAndSet = (v: UserAuthType) => {
        const next = validate(v);
        setFieldsError(next);
        return next;
    };

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const {name, value} = e.target;
        const field = name as FieldName;

        setValueState((prev) => {
            const updated = { ...prev, [field]: value};

            if(touched[field]){
                setFieldsError(validate(updated));
            }

            return updated;
        });
    };


    const handleBlur = (e: React.FocusEvent<HTMLInputElement>) => {
        const field = e.target.name as FieldName;

        touchField(field);
        validateAndSet(valueState);
    };


    const handleSubmit = async(e) => {
        e.preventDefault();
        setErrorState("");
        setTuched({
            fullName: true,
            username: true,
            password: true,
            confirmPassword: true,
        });

        const nextErrors = validateAndSet(valueState);
        if(Object.keys(nextErrors).length > 0) return;

        const {confirmPassword, ...registrationData} = valueState;

        try{
            setLoading(true);
            await apiPost("/registration", registrationData);
            showFlash("success", "Registrace proběhla úspěšně")
            navigate("/login");

        }catch (e){
            if(e instanceof HttpRequestError){
                setErrorState(e.message || "Registrace se nepovedla");
                showFlash("error", e.message || "Registrace se nepovedla");

                const d = e.details as any;
                const fieldMap = d?.errors || d?.fieldErrors;
                if(fieldMap && typeof fieldMap === "object"){
                    setFieldsError(fieldMap);
                    setTuched((prev) => ({...prev, ...Object.keys(fieldMap)}))
                }
            }else {
                setErrorState("Registrace se nepovedla");
                showFlash("error", "Registrace se nepovedla");
            }
        }finally{
            setLoading(false);
        }
    };
    
    const showError = (field: FieldName) => touched[field] && fieldError[field];

    return(
        <>
            <RegistrationPanel
            onSubmit = {handleSubmit}
            errorState = {errorState}
            onChange = {handleChange}
            onBlur = {handleBlur}
            valueState = {valueState}
            loading = {loading}
            fieldError = {fieldError}
            showError = {showError}
            setField = {setField}
            touch = {touchField}
            />
        </>
    )
}
export default RegistrationPage;