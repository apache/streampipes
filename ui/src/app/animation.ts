import {animate, animateChild, group, query, style, transition, trigger} from "@angular/animations";

export const slideInAnimation =
    trigger('routeAnimations', [
      transition('* <=> LoginPage', [
        style({ position: 'relative' }),
        query(':enter, :leave', [
          style({
            position: 'absolute',
            top: 0,
            left: 0,
            width: '100%'
          })
        ], { optional: true }),
        query(':enter', [
          style({ opacity: 0 })
        ], { optional: true }),
        query(':leave', animateChild(), { optional: true }),
        group([
          query(':leave', [
            animate('200ms', style({ opacity: 0 }))
          ], { optional: true }),
          query(':enter', [
            animate('300ms', style({ opacity: 1 }))
          ],{ optional: true })
        ]),
        query(':enter', animateChild(), { optional: true }),
      ])
    ]);