import { RouterModule, Routes } from '@angular/router';
import { Login } from '../auth/login/login';
import { Register } from '../auth/register/register';
import { NgModule } from '@angular/core';
import { Home } from '../pages/home/home';
import { Dashboard } from '../pages/dashboard/dashboard';
import { Profile as ProfileComponent } from '../pages/profile/profile';
import { authGuard } from './guard';
import { Explore } from '../pages/explore/explore';
import { Post as postDetailComponent } from '../pages/post/post';
import { Notfound } from '../pages/notfound/notfound'
import { Unauthorized } from '../pages/unauthorized/unauthorized'

import { Banned } from '../pages/banned/banned'
import { CreatePost } from '../pages/create-post/create-post'
import { Forbiden } from '../pages/forbiden/forbiden';
export const routes: Routes = [
    {
        path: '',
        component: Home,
        canActivate: [authGuard]
    },
    {
        path: 'login',
        component: Login,
        canActivate: [authGuard]
    },
    {
        path: 'register',
        component: Register,
        canActivate: [authGuard]
    },
    {
        path: 'home',
        component: Home,
        canActivate: [authGuard]
    },
    {
        path: 'create-post',
        component: CreatePost,
        canActivate: [authGuard]
    },
    {
        path: 'edit-post/:id',
        component: CreatePost,
        canActivate: [authGuard]
    },
    {
        path: 'dashboard',
        component: Dashboard,
        canActivate: [authGuard]
    },
    {
        path: 'explore',
        component: Explore,
        canActivate: [authGuard]
    },
    {
        path: 'banned',
        component: Banned,
        canActivate: [authGuard]
    },
    {
        path: 'profile/:username',
        component: ProfileComponent,
        canActivate: [authGuard]
    },
    {
        path: 'post/:id',
        component: postDetailComponent,
        canActivate: [authGuard]
    },
    {
        path: 'forbiden',
        component: Forbiden,
    }, {
        path: 'unauthorized',
        component: Unauthorized,
    },
    {
        path: '404',
        component: Notfound,
    },
    {
        path: '**',
        redirectTo: '404'
    },
];


@NgModule({
    imports: [RouterModule.forRoot(routes)],
    exports: [RouterModule]
})

export class AppRoutingModule { }
