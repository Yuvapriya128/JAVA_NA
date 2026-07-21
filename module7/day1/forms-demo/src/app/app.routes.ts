import { Routes } from '@angular/router';
import { Normalform } from './normalform/normalform';
import { Specialform } from './specialform/specialform';
import { Reactiveform } from './reactiveform/reactiveform';
import { Structuraldirectives } from './structuraldirectives/structuraldirectives';
import { Attributedirectives } from './attributedirectives/attributedirectives';
import { Pipesdemo } from './pipesdemo/pipesdemo';

export const routes: Routes = [
    {path:'',component:Normalform},
    {path:'special',component:Specialform},
    {path:'reactive',component:Reactiveform},
    {path:'structural',component:Structuraldirectives},
    {path:'attribute', component:Attributedirectives},
    {path:'pipesdemo',component:Pipesdemo}

];
