package com.example.autogestinventory.navigation

import RegisterScreen
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.autogestinventory.Screens.LoginScreen
import com.example.autogestinventory.views.AuthViews.SplashView
import com.example.autogestinventory.views.ModulosViews.CategoriasView
import com.example.autogestinventory.views.ModulosViews.ConfiguracionView
import com.example.autogestinventory.views.ModulosViews.KardexView
import com.example.autogestinventory.views.ModulosViews.MarcaView
import com.example.autogestinventory.views.ModulosViews.PersonalView
import com.example.autogestinventory.views.ModulosViews.ProductosView
import com.example.autogestinventory.views.ModulosViews.TuEmpresaView

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavManager(){
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "splash"){
        composable("splash"){
            SplashView(navController)
        }
        composable("login") {
            LoginScreen(navController)
        }
        composable("register") {
            RegisterScreen(navController)
        }
        composable("configuracion"){
            ConfiguracionView(navController)
        }
        composable("tuempresa"){
            TuEmpresaView(navController)
        }
        composable("marca"){
            MarcaView(navController)
        }
        composable("categorias") {
                CategoriasView(navController)
        }
        composable("productos") {
            ProductosView(navController)
        }
        composable("personal") {
            PersonalView(navController)
        }
        composable("kardex") {
            KardexView(navController)
        }
    }
}